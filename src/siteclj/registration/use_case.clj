(ns siteclj.registration.use-case
  (:require
    [siteclj.db :as db]
    [siteclj.valid :as sv]))

(defn uuid  []  (str  (java.util.UUID/randomUUID)))

(defn email-uniq? [email]
  (nil? (db/q-one* {:select [:*]
                    :from [:users]
                    :where [:= :email email]})))

(defn login-uniq? [x]
  (nil? (db/q-one* {:select [:*]
                    :from [:users]
                    :where [:= :login x]})))

(defn start-registration [email]
  (if email
    (if (email-uniq? email)
      (first (db/i!  :registrations
                    {:email email
                     :activation_key (uuid)}))
      {:status :error :message (str "Email [" email "] has already taken")})
    {:status :error :message "Email required"}))

(defn registration-by-key [k]
  (db/q-one*
    {:select [:*]
     :from [:registrations]
     :where [:= :activation_key k]}))

(defn check-activation-key [data]
  (if-let [registration
           (db/q-one* {:select [:*]
                       :from   [:registrations]
                       :where  [:= :activation_key (:activation_key data)]})]
    (assoc data registration)
    {:status :error :message (str "No registration for key " (:key data))}))

(def Registration
  {:login [sv/not-blank? login-uniq?]
   :email [sv/not-blank? email-uniq?]
   :password [sv/not-blank?]
   [:password :password_confirmation] [=]})

(def UserAttrs [:login :password :email :family_name :given_name :organization])

(defn complete-registration [{akey :activation_key :as user}]
  (if-let [regi (registration-by-key akey)]
    (let [user (merge (dissoc user :activation_key) {:email (:email regi)})
          errors (sv/validate Registration user)]
      (if (empty? errors)
        (db/i! :users (select-keys user UserAttrs))
        {:status :error :errors errors}))
    {:status :error :message "No pending registration"}))

(defn user-by-cred [login password]
  (db/q-one*
    {:select [:*]
     :from [:users]
     :where [:and [:or [:= :login login] [:= :email login]]
             [:= :password password]]}))

(defn start-session [{email :email password :password}]
  (if-let [user (user-by-cred email password)]
    (first (db/i! :sessions {:id (uuid) :user_id (:id user)}))
    {:status :error :message "Credentials are wrong"}))

(defn stop-session [sid]
  (db/d! :sessions ["id=?" sid]))

(defn user-by-session [sid]
  (db/q-one*
    {:select [:u.*]
     :from [[:users :u] [:sessions :s]]
     :where [:and
             [:= :u.id :s.user_id]
             [:= :s.id sid]]}))

(comment
  (start-registration {:email "nos@gmail.com"})
  (activation {:activation_key "0a95d59b-e24b-494b-86e5-364e0162dfb9"})

  (db/q*
    {:select [:*]
     :from [:users]}))
