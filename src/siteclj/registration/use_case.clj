(ns siteclj.registration.use-case
  (:require
    [siteclj.valid :as sv]))

(def members (atom {}))

(def Registration
  {:login [sv/not-blank?]
   :password [sv/not-blank?]
   :password_confirmation [sv/not-blank?]
   [:password :password_confirmation] [=]})

(defn register [x]
  (let [errors (sv/validate Registration x)]
    (if (empty? errors)
      {:status :ok
       :member (swap! members assoc (:login x) x)}
      {:status :error
       :member x
       :errors errors})))

(comment
  (println members))
