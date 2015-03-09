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
  (let [res (sru/register params)]
    (if (= :ok (:status res))
      (-> (rur/redirect "/") (assoc :flash "Created"))
      (ok (v/sign-up (merge req res))))))

(def routes
  {:GET {:fn #'sign-up}
   :POST {:fn #'sign-up!}})
