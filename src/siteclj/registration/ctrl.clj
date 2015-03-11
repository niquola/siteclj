(ns siteclj.registration.ctrl
  (:require [ring.util.response :as rur]
            [siteclj.registration.use-case :as sru]
            [siteclj.registration.views :as v]))

(defn ok [cnt]
  {:body  cnt
   :status 200})

(defn sign-up  [{params :params :as req}]
  (ok (v/sign-up req)))



(defn sign-up!  [{params :params :as req}]
  (let [res (sru/complete-registration params)]
    (if (= :error (:status res))
      (ok (v/fill-profile (merge req res {:member params})))
      (-> (rur/redirect "/sign-up/sign-in") (assoc :flash "Created")))))

(defn register [{{akey :activation_key} :params :as req}]
  (let [regi (sru/registration-by-key akey)]
    (ok (v/fill-profile (assoc req :registration regi)))))

(defn sign-up-in! [{{pass :password email :email :as params} :params :as req}]
  (if (and pass (not (sru/email-uniq? email)))
    ;; sign in
    (let [res (sru/start-session params)]
      (if (= (:status res) :error)
        (-> (rur/redirect  (str "/?error=" (:message res))))
        (-> (rur/redirect "/")
            (assoc :session {:id (:id res)}))))
    ;; sign up
    (let [res (sru/start-registration email)]
      (if (= (:status res) :error)
        (-> (rur/redirect "/")
            (assoc :flash (:message res)))
        (ok (v/key-emailed (assoc req :data res)))))))

(defn sign-out [{{sid :id} :session :as req}]
  (sru/stop-session sid)
  (-> (rur/redirect "/")
      (assoc :destroy_session true)))

(defn user-by-session [sid]
  (sru/user-by-session sid))

(defn profile [req]
  (ok (v/profile req)))

(def routes
  {:GET {:fn #'sign-up}
   :POST {:fn #'sign-up-in!}
   "profile" {:GET {:fn #'profile}}
   "sign-out" {:GET {:fn #'sign-out}}
   [:activation_key] {:GET {:fn #'register}
                      :POST {:fn #'sign-up!}}})
