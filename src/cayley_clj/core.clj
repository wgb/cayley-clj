(ns cayley-clj.core
  (:require [clojure.string :as s]
            [clojure.data.json :as json]
            [cayley-clj.http :as h]))


(defn- shape-arg [a]
  (cond
    (nil? a) "null"
    (integer? a) a
    :else (format "\"%s\"" a)))

(defn- shape-args
  [args]
  (if (coll? args)
    (map shape-arg args)
    (shape-arg args)))

(defn- shape-verbs
  [[fname & args]]
  (if args
    (if-not (vector? (first args))
      (if (contains? #{:Follow :FollowR :Intersect :Union} fname)
        (format "%s(%s)" (name fname) (first args))
        (format "%s(%s)" (name fname) (shape-args (first args))))
      (if (empty? (first args))
        (format "%s()" (name fname))
        (if (keyword? (ffirst args))
          (format "%s(%s)" (name fname) (s/join "." (map shape-verbs args)))
          (format "%s(%s)" (name fname) (s/join "," (shape-args (first args)))))))
    (name fname)))

(defn- to-gremlin
  [q]
  (s/join "." (map shape-verbs q)))

(defn- build-path
  [[varname path]]
  (format "var %s = %s" varname (to-gremlin path)))

(defn query
  [q url & paths]
  (if-let [gremlin-paths (map build-path paths)]
    (let [paths-and-query (s/join "\r\n" (reverse (conj gremlin-paths (to-gremlin q))))]
      (h/send paths-and-query url))
    (h/send (to-gremlin q) url)))

(defn- shape-triple-quad
  [triple-quad]
  (let [triple-keys [:subject :predicate :object :provenance]]
    (zipmap triple-keys triple-quad)))

(defn write
  [triples-quads url]
  (h/send (json/write-str (map shape-triple-quad triples-quads)) url))
