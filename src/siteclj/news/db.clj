(ns siteclj.news.db
  (:require
    [siteclj.db :as db]
    [clojure.java.jdbc :as jdbc]))

(def base-query
  {:select [:*]
   :from [:news]})

(defn list-news [& [params]]
  (db/q*
    (merge
      base-query
      {:order [:updated_at :desc]})))

(defn get-news [id]
  (db/q-one*
    (merge
      base-query
      {:where [:= :id id]})))

(defn save-news [data]
  (db/i! :news data))

(defn migrate []
  (db/e!
    (jdbc/create-table-ddl
      :news
      [:id :SERIAL "PRIMARY KEY"]
      [:user_id :bigint]
      [:title :text "NOT NULL"]
      [:abstract :text "NOT NULL"]
      [:content  :text  "NOT NULL"]
      [:created_at :timestamp "DEFAULT CURRENT_TIMESTAMP"]
      [:updated_at :timestamp "DEFAULT CURRENT_TIMESTAMP"])))

(defn rollback []
  (db/e! "DROP TABLE IF EXISTS news"))

(comment
  (rollback)
  (migrate)
  (db/i!
    :news
    {:title "New article" :abstract "abstract" :content "Content"}))
