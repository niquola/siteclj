(ns siteclj.registration.ctrl
  (:require [ring.util.response :as rur]
            [siteclj.registration.use-case :as sru]
            [siteclj.registration.views :as v]))

(defn ok [cnt]
  {:body  cnt
   :status 200})

(defn sign-up  [{params :params :as req}]
  (ok (v/sign-up req)))

(defn start [{{email :email} :params :as req}]
  (let [res (sru/start-registration email)]
    (if (= (:status res) :error)
      (ok (v/sign-up (assoc req :error (:message res))))
      (ok (pr-str res)))))

(defn sign-up!  [{params :params :as req}]
  (let [res (sru/register params)]
    (if (= :ok (:status res))
      (-> (rur/redirect "/") (assoc :flash "Created"))
      (ok (v/sign-up (merge req res))))))

(defn register [{{akey :activation_key} :params}]
  (ok (pr-str (sru/registration-by-key akey))))

(def routes
  {:GET {:fn #'sign-up}
   "start" {:POST {:fn #'start}}
   :POST {:fn #'sign-up!}
   [:activation_key] {:GET {:fn #'register}}
   })
