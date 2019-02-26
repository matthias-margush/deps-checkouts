(ns scratch
  (:require [clojure.pprint :refer [pprint]]
            [clojure.tools.deps.alpha :as deps]
            [clojure.tools.deps.alpha.extensions :as ext]
            [clojure.tools.deps.alpha.reader :as deps-reader]))

(defn foo [deps]
  (let [deps (:deps deps) #_(deps/resolve-deps deps nil)]
    (for [[lib coord] deps]
      (ext/manifest-type lib coord nil))))

(comment
  (def deps (deps-reader/slurp-deps "testdeps.edn"))
  (deps/resolve-deps deps nil)

  (foo deps)

  (->
   '{:deps {defnspecken {:git/url "git@github.com:matthias-margush/defnspecken.git"
                         :sha "c83fd6752202df1ad0801f5c9b3ab4aa12c71c5b"}
            org.clojure/core.async {:mvn/version "0.4.474"}}}
   (deps/resolve-deps nil))

  #_(and (:local/root coord)
         (= :deps (:deps/manifest (ext/manifest-type lib coord config))))

  (for [[lib coord] resolved-deps]
    (ext/manifest-type lib coord {}))) ;; => nil
;; => nil
