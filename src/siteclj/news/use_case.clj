(ns siteclj.news.use-case
  (:require
    [siteclj.valid :as sv]
    [siteclj.news.db :as snd]))

(def members (atom {}))

(def Registration
  {:login [sv/not-blank?]
   :password [sv/not-blank?]
   :password_confirmation [sv/not-blank?]
   [:password :password_confirmation] [=]})

(defn list-news [params]
  (snd/list-news params))

(defn show [{id :news-id}]
  (snd/get-news id))

(defn save [data]
  (snd/save-news data))

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
