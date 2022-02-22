(ns cljdoc-shared.analysis-test
  (:require [clojure.test :as t]
            [cljdoc-shared.analysis :as analysis]))

(t/deftest cldoc-edn-test
  (t/is (= "cljdoc-analysis-edn/gid1/art1/v1/cljdoc-analysis.edn" (analysis/result-file 'gid1/art1 "v1")))
  (t/is (= "cljdoc-analysis-edn/art2/art2/v2/cljdoc-analysis.edn" (analysis/result-file 'art2 "v2"))))
