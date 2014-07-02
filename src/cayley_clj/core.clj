(ns cayley-clj.core
  (:require [clojure.string :as s]
            [cayley-clj.http :as h]))

(defn- shape-args
  [args]
  (if (coll? args)
    (map (fn [a]
           (if (integer? a)
             a
             (format "\"%s\"" a))) args)
    (if (integer? args)
      args
      (format "\"%s\"" args))))

;; (defn- verb-type
;;   [[fname & args]]
;;   (print-str [fname args])
;;   (cond
;;    (= :Follow fname) :verb-path
;;    (string? fname) :obj
;;    (and (keyword? fname) args (coll? (first args)) (keyword? (ffirst args))) :verb-fn-args
;;    (and (keyword? fname) args (coll? (first args))) :verb-args
;;    (and (keyword? fname) (nil? args)) :verb
;;    (and (keyword? fname) args) :verb-arg
;;    :else :verb))

;; (defmulti shape-verbs
;;   (fn [x] (verb-type x)))

;; (defmethod shape-verbs :verb
;;   [[fname & args]]
;;   (format "%s()" (name fname)))

;; (defmethod shape-verbs :obj
;;   [[fname & args]]
;;   (format "%s" fname))

;; (defmethod shape-verbs :verb-arg
;;   [[fname & args]]
;;   (format "%s(%s)" (name fname) (shape-args (first args))))

;; (defmethod shape-verbs :verb-args
;;   [[fname & args]]
;;   (format "%s(%s)" (name fname) (s/join "," (shape-args (first args)))))

;; (defmethod shape-verbs :verb-fn-args
;;   [[fname & args]]
;;   (format "%s(%s)" (name fname) (shape-verbs (first args))))

;; (defmethod shape-verbs :default
;;   [[fname & args]]
;;   (str fname))

(defn- shape-verbs
  [[fname & args]]
  (if (keyword? fname)
    (if args
      (if-not (vector? (first args))
        (if (contains? #{:Follow :FollowR :Intersect :Union} fname)
          (format "%s(%s)" (name fname) (first args))
          (format "%s(%s)" (name fname) (shape-args (first args))))
        (if (keyword? (ffirst args))
          (format "%s(%s)" (name fname) (shape-verbs (first args)))
          (format "%s(%s)" (name fname) (s/join "," (shape-args (first args))))))
      (format "%s()" (name fname)))
    (format "%s" fname)))

(defn to-gremlin
  [q]
  (s/join "." (map shape-verbs q)))

(defn build-path
  [[varname path]]
  (format "var %s = %s" varname (to-gremlin path)))

(defn query
  [q url & paths]
  (if-let [gremlin-paths (map build-path paths)]
    (let [paths-and-query (s/join "\r\n" (reverse (conj gremlin-paths (to-gremlin q))))]
      (h/send paths-and-query url))
    (h/send (to-gremlin q) url)))
