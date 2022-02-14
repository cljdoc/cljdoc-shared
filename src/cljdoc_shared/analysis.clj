(ns cljdoc-shared.analysis
  (:require [cljdoc-shared.proj :as proj]))

(def output-prefix
  "Location of analysis results.

  Must match cljdoc/builder circleci yml output location"
  "/tmp/cljdoc/analysis-out/")

(defn result-file
  [project version]
  {:pre [(some? project) (string? version)]}
  (str "cljdoc-edn/" (proj/group-id project) "/" (proj/artifact-id project) "/" version "/cljdoc.edn"))

(defn result-path
  [project version]
  {:pre [(some? project) (string? version)]}
  (str output-prefix (result-file project version)))
