(ns ^:no-doc cljdoc-shared.spec.analyzer
  (:refer-clojure :exclude [assert])
  (:require [clojure.spec.alpha :as s]))

(s/def ::platform #{"clj" "cljs"})

;; analysis -------------------------------------------------------------
;; A spec for analysis namespace analysis data

(s/def :cljdoc.analysis.public/name symbol? #_(s/or :a string? :b symbol?))
(s/def :cljdoc.analysis.public/file string?)
(s/def :cljdoc.analysis.public/line int?)
(s/def :cljdoc.analysis.public/arglists coll?)
(s/def :cljdoc.analysis.public/doc (s/nilable string?))
(s/def :cljdoc.analysis.public/type #{:var :fn :macro :protocol :multimethod})
(s/def :cljdoc.analysis.public/members (s/coll-of :cljdoc.analysis/public))

(s/def :cljdoc.analysis/public
  (s/keys :req-un [:cljdoc.analysis.public/name
                   :cljdoc.analysis.public/type]
          :opt-un [:cljdoc.analysis.public/deprecated
                   :cljdoc.analysis.public/doc
                   :cljdoc.analysis.public/arglists
                   :cljdoc.analysis.public/file
                   :cljdoc.analysis.public/line
                   :cljdoc.analysis.public/members]))

(s/def :cljdoc.analysis.namespace/name symbol?)
(s/def :cljdoc.analysis.namespace/publics (s/coll-of :cljdoc.analysis/public))
(s/def :cljdoc.analysis.namespace/doc (s/nilable string?))

(s/def :cljdoc.analysis/namespace
  (s/keys :req-un [:cljdoc.analysis.namespace/name
                   :cljdoc.analysis.namespace/publics]
          :opt-un [:cljdoc.analysis.namespace/doc]))


;; api analysis produced cljdoc.edn ---------------------------------------------------------
;; (not the user's repo cljdoc.edn)

(s/def :cljdoc.cljdoc-edn/analysis
  (s/map-of ::platform (s/coll-of :cljdoc.analysis/namespace)))

(s/def :cljdoc.cljdoc-edn/pom-str string?)

(s/def :cljdoc/cljdoc-edn
  (s/keys :req-un [:cljdoc.cljdoc-edn/analysis
                   :cljdoc.cljdoc-edn/pom-str]))

;; Publicly used asssertions:

(defn assert-result-namespaces [v]
  (s/assert :cljdoc.cljdoc-edn/analysis v))

(defn assert-result-full [v]
  (s/assert :cljdoc/cljdoc-edn v))
