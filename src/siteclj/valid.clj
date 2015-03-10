(ns siteclj.valid
  (:require [clojure.string :as cs]))

(defn not-blank? [x]
  (not (cs/blank? x)))

(defn get-vals [m ks]
  (map
    (fn [k] (get m k))
    ks))

(defn add-error [es k e]
  (if (get es k)
    (update-in es [k] conj e)
    (assoc es k [e])))

(defn validate-key [er x k v]
  (if (vector? k)
    (if (apply v (get-vals x k))
      er
      (add-error er k :invalid))
    (if (v (get x k))
      er
      (add-error er k :invalid))))

(defn validate [vs x]
  (reduce
    (fn [er [k v]]
      (reduce (fn [er v] (validate-key er x k v)) er v)
      ) {} vs))

(defn validation [h validator]
  (fn [data]
    (let [errors (validate validator data)]
      (if (empty? errors)
        (h data)
        {:status :error :data data :errors errors}))))
