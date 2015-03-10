(ns siteclj.registration.use-case
  (:require
    [siteclj.db :as db]
    [siteclj.use-case :as su]
    [siteclj.valid :as sv]))

(defn uuid  []  (str  (java.util.UUID/randomUUID)))

(defn email-uniq? [data]
  (if (db/q-one* {:select [:*]
                  :from [:users]
                  :where [:= :email (:email data)]})
    {:status :error :message (str "Email [" (:email data) "] already taken")}
    data))

(defn save-registration [data]
  (db/i! :registrations
         (merge
           (select-keys data [:email])
           {:activation_key (uuid)})))


(defn check-activation-key [data]
  (if-let [registration
           (db/q-one* {:select [:*]
                       :from   [:registrations]
                       :where  [:= :activation_key (:activation_key data)]})]
    (assoc data registration)
    {:status :error :message (str "No registration for key " (:key data))}))

(defn user-from-registration [req]
  (db/i!
    (merge (select-keys reg [:email]))))

(su/defcase start-registration
  (sv/validation {:email [sv/not-blank?]} )
  (su/intercept-error email-uniq?)
  save-registration)

(su/defcase start-activation
  (sv/validation {:activation_key [sv/not-blank?]})
  (su/intercept-error check-activation-key))

(su/defcase register
  (sv/validation {:login [sv/not-blank?]
                  :email [sv/not-blank?]
                  :password [sv/not-blank?]
                  [:password :password_confirmation] [=]
                  :activation_key [sv/not-blank?]})
  (su/intercept-error check-activation-key))

(comment
  (start-registration {:email "nos@gmail.com"})
  (activation {:activation_key "0a95d59b-e24b-494b-86e5-364e0162dfb9"})

  (db/q*
    {:select [:*]
     :from [:registrations]}))
