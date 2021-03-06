(ns siteclj.registration.db
  (:require
    [siteclj.db :as db]
    [clojure.java.jdbc :as jdbc]))

(defn uuid  []  (str  (java.util.UUID/randomUUID)))

(defn save-registration [data]
  (db/i! :registrations data))

(defn create-user [data]
  (db/i! :users data))

(defn check-credentials [login password]
  (db/q-one*
    {:select [:*] :from [:users]
     :where [:and
             [:= :active true]
             [:or
              [:= :login login]
              [:= :email login]]
             [:= :password password]]
     :limit 1}))



(defn rollback []
  (->>
    (map
      (fn [tbl] (str "DROP TABLE IF EXISTS " (name tbl) " CASCADE; "))
      [:users :registrations :sessions_history :sessions])
    (apply str)
    (db/e!)))

(defn migrate []
  (db/e!
    (jdbc/create-table-ddl
      :users
      [:id :SERIAL "PRIMARY KEY"]
      [:admin :boolean]
      [:active :boolean "DEFAULT true"]
      [:first_name :text]
      [:last_name :text]
      [:phone :text]
      [:organization :text]
      [:email :text "NOT NULL" "UNIQUE"]
      [:login :text "NOT NULL" "UNIQUE"]
      [:password :text]))

  (db/e!
    (jdbc/create-table-ddl
      :sessions
      [:id :uuid "PRIMARY KEY"]
      [:user_id :bigint]
      [:started_at :timestamp "DEFAULT CURRENT_TIMESTAMP"]
      [:expires_at :timestamp]
      [:completed_at :timestamp]))

  (db/e!
    (jdbc/create-table-ddl
      :registrations
      [:id :SERIAL "PRIMARY KEY"]
      [:active :boolean "DEFAULT true"]
      [:email :text "NOT NULL"]
      [:activation_key :text "NOT NULL"]
      [:created_at :timestamp "DEFAULT CURRENT_TIMESTAMP"])))

(comment
  (rollback)
  (migrate))
