(ns deps.checkouts
  "Utilities for managing local tools.deps checkouts."
  (:require [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.tools.deps.alpha :as deps]
            [clojure.tools.deps.alpha.extensions :as ext]
            [clojure.tools.deps.alpha.reader :as deps-reader])
  (:import [java.nio.file Files Paths]
           [java.nio.file.attribute FileAttribute]))

(defn- local-dep?
  "Checks whether the library has a local dependency coordinate."
  [lib coord config]
  (and (:local/root coord)
       (= :deps (:deps/manifest (ext/manifest-type lib coord config)))))

(defn- link!
  "Creates a symbolic link to the dependency under `root`."
  [root lib coord config]
  (when (local-dep? lib coord config)
    (let [source-path (.getCanonicalPath (io/file (:local/root coord)))
          target (io/file root (str lib))
          target-path (.getCanonicalPath target)
          target-root (.getParent (io/file (.getCanonicalPath target)))]
      (when-not (.exists (io/file target-root))
        (io/make-parents target-root)
        (printf "Linking %s to %s\n" source-path target-root)
        (Files/createSymbolicLink (.toPath (io/file target-root))
                                  (.toPath (io/file source-path))
                                  (into-array FileAttribute []))))))

(defn- link-local-deps!
  "Links all local dependencies in `deps-edn` under `root`."
  [root deps-edn config]
  (doseq [[lib coord] (:deps deps-edn)]
    (link! root lib coord config)))

(defn -main
  [& args]
  (when (and ((set args) "--link") ((set args) "--all"))
    (let [deps (deps-reader/slurp-deps (io/file "deps.edn"))]
      (link-local-deps! "deps" deps nil))))
