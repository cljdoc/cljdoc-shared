(ns ^:no-doc cljdoc-shared.proj
  "This would have been named project.clj but cider assumed leiningen project, hence proj.clj")

(defn group-id [project]
  (or (if (symbol? project)
        (namespace project)
        (namespace (symbol project)))
      (name project)))

(defn artifact-id [project]
  (name (symbol project)))

(defn normalize [project]
  (str (group-id project) "/" (artifact-id project)))

(defn clojars-id [{:keys [group-id artifact-id]}]
  (if (= group-id artifact-id)
    artifact-id
    (str group-id "/" artifact-id)))
