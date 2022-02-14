(ns cljdoc-shared.pom
  "Functions to parse POM files and extract information from them."
  (:require [clojure.string :as string])
  (:import (org.jsoup Jsoup)))

(defn- text [^Jsoup doc sel]
  (when-let [t (some-> (.select doc sel) (first) (.ownText))]
    (when-not (string/blank? t) t)))

(defn- artifact-info [^Jsoup doc]
  ;; These `parent` fallbacks are a bit of a hack but
  ;; I didn't want to modify the data model and make this
  ;; leak outside of this namespace - Martin
  ;;
  ;; TODO: are the parent fallbacks used/necessary? If so, when/how?
  {:group-id    (or (text doc "project > groupId")
                    (text doc "project > parent > groupId"))
   :artifact-id (text doc "project > artifactId")
   :version     (or (text doc "project > version")
                    (text doc "project > parent > version"))
   :description (text doc "project > description")
   :url         (text doc "project > url")})

(defn- scm-info [^Jsoup doc]
  {:url (text doc "project > scm > url")
   :sha (text doc "project > scm > tag")})

(defn- managed-deps-tree
  "Return a tree structure allowing easy access to version numbers for artifacts specified via
  <dependencyManagement>. This key is sometimes used by projects to specify dependency versions
  in a central place instead of repeating them in each .pom file.

  See [the official Maven docs](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Management)."
  [^Jsoup doc]
  (reduce (fn [tree d]
            (assoc-in tree [(text d "groupId") (text d "artifactId")] (text d "version")))
          {}
          (.select doc "project > dependencyManagement > dependencies > dependency")))

(defn- dependencies [^Jsoup doc]
  (for [d (.select doc "project > dependencies > dependency")]
    {:group-id    (text d "groupId")
     :artifact-id (text d "artifactId")
     :version     (text d "version")
     :scope       (text d "scope")
     :optional    (text d "optional")}))

(defn- dependencies-with-versions
  "Like the regular [[dependencies]]) but also takes into account version numbers specified via
  managed dependencies ([[managed-deps-tree]]). If a dependency returned by [[dependencies]]
  does not contain a version it will be looked up via the return value of [[managed-deps-tree]].
  This ensures a non-nil `:version` key is always present for all dependencies.

  TODO: Why aren't we using clojure tools deps to get real dep versions? Just asking, there might be a good reason.
        This would likely handle nuances we have not considered."
  [^Jsoup doc]
  (let [versions-tree (managed-deps-tree doc)]
    (->> (dependencies doc)
         (map (fn [{:keys [group-id artifact-id version] :as d}]
                (if-not version
                  (assoc d :version (get-in versions-tree [group-id artifact-id]))
                  d))))))

(defn- repositories [^Jsoup doc]
  (for [r (.select doc "project > repositories > repository")]
    {:id (text r "id")
     :url (text r "url")}))

(defn parse [pom-str]
  (let [d (Jsoup/parse pom-str)]
    {:scm (scm-info d)
     :artifact-info (artifact-info d)
     :dependencies (dependencies-with-versions d)
     :repositories (repositories d)}))
