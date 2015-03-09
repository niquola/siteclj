(ns siteclj.db.coerce
  (:require
    [clj-time.core :as t]
    [clj-time.coerce :as tc]
    [clojure.string :as cs])
  (:import (org.joda.time DateTime)
           (java.sql Timestamp)
           (java.util Date)
           (org.postgresql.jdbc4 Jdbc4Array)
           (org.postgresql.util PGobject)))

(defn- sql-time-to-clj-time [sql-time]
  (tc/from-sql-time sql-time))

(defn- clj-time-to-sql-time [clj-time]
  (tc/to-sql-time clj-time))

(defn- quote-seq [v]
  (str "{" (cs/join "," (map #(str "\"" % "\"") v)) "}"))

(defn- map-map [m map-fn]
  (reduce (fn [new-map [k v]]
            (assoc new-map k (map-fn v)))
          {} m))

(defmulti to-jdbc class)

(defmethod to-jdbc clojure.lang.PersistentArrayMap [m] (map-map m to-jdbc))
(defmethod to-jdbc clojure.lang.PersistentHashMap [m] (map-map m to-jdbc))

(defmethod to-jdbc clojure.lang.Keyword [v]
  (name v))

(defmethod to-jdbc org.joda.time.DateTime [v]
  (clj-time-to-sql-time v))

(defmethod to-jdbc java.util.Date [v]
  (java.sql.Timestamp. (.getTime v)))

(defmethod to-jdbc clojure.lang.PersistentVector [v]
  (quote-seq v))

(defmethod to-jdbc clojure.lang.PersistentList [v]
  (quote-seq v))

(defmethod to-jdbc clojure.lang.PersistentHashSet [s]
  (quote-seq s))

(defmethod to-jdbc :default [v] v)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti from-jdbc class)

(defmethod from-jdbc clojure.lang.PersistentArrayMap [m] (map-map m from-jdbc))
(defmethod from-jdbc clojure.lang.PersistentHashMap [m] (map-map m from-jdbc))

#_(defmethod from-jdbc org.postgresql.util.PGobject [v]
    (if (= (.getType v) "json")
      (json/parse (.toString v))
      (.toString v)))

(defmethod from-jdbc org.postgresql.jdbc4.Jdbc4Array [v]
  (vec (.getArray v)))

(defmethod from-jdbc java.sql.Timestamp [v]
  (sql-time-to-clj-time v))

(defmethod from-jdbc :default [v] v)
