(ns siteclj.db
  (:require
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as str]
    [honeysql.core :as sql]
    [siteclj.db.coerce :as sdc]))

(def db-spec
  {:subprotocol "postgresql"
   :subname "//localhost:5432/siteclj"
   :classname "org.postgresql.Driver"
   :user "user"
   :password "user"
   :stringtype "unspecified"})

(def ^:dynamic *db* db-spec)

(defmacro with-db [db & body]
  `(binding [*db* ~db]
     ~@body))

(defmacro with-connection [[binding] & body]
  `(if-let [conn# (:connection *db*)]
     (let [~(symbol binding) conn#] ~@body)
     (with-open [conn#  (jdbc/get-connection *db*)]
       (let [~(symbol binding) conn#] ~@body))))

(defn- coerce-query-args [sql]
  (let [[stmt & args] (if (coll? sql) sql [sql])
        coerced-args (map sdc/to-jdbc args)]
    (into [stmt] coerced-args)))

(defmacro report-actual-sql-error [& body]
  `(try
     ~@body
     (catch java.sql.SQLException e#
       (if (.getNextException e#) ;; rethrow exception containing SQL error
         (let [msg# (.getMessage (.getNextException e#))]
           (throw (java.sql.SQLException.
                    (str (str/replace (.getMessage e#)
                                      "Call getNextException to see the cause." "")
                         "\n" msg#))))
         (throw e#)))))

(defn e! [& cmd]
  (println "SQL:" cmd)
  (report-actual-sql-error
    (if (vector? (first cmd))
      (apply jdbc/execute! *db* cmd)
      (jdbc/execute! *db* cmd))))

(defn q [sql]
  (println "SQL:" sql)
  (report-actual-sql-error
    (jdbc/query *db* (coerce-query-args sql) :row-fn sdc/from-jdbc)))

(defn q* [hsql]
  (println (sql/format hsql))
  (q (sql/format hsql)))

(defn q-one* [hsql]
  (first (q* hsql)))


(defn i! [tbl & row-maps]
  (let [coerced-rows (map sdc/to-jdbc row-maps)]
    (println "INSERT INTO" tbl (pr-str coerced-rows))
    (report-actual-sql-error
      ;; perform insert and coerce results from jdbc
      (map sdc/from-jdbc
           (apply jdbc/insert! *db* tbl coerced-rows)))))

(defn d! [tbl & args]
  (println "DELETE FROM:" tbl (pr-str args))
  (report-actual-sql-error
    (first (apply jdbc/delete! *db* tbl (coerce-query-args args)))))
