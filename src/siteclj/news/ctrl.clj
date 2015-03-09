(ns siteclj.news.ctrl
  (:require [ring.util.response :as rur]
            [siteclj.news.use-case :as snu]
            [siteclj.news.views :as v]))

(defn ok [cnt]
  {:body  cnt
   :status 200})

(defn list-news  [{params :params :as req}]
  (ok (v/index (assoc req :news (snu/list-news params)))))

(defn show-news  [{params :params :as req}]
  (ok (v/show (assoc req :news (snu/show params)))))

(defn new-form  [{params :params :as req}]
  (ok (v/new-form (assoc req :news (snu/show params)))))

(defn create-news  [{params :params :as req}]
  (let [entry (snu/save params)]
    (rur/redirect (str "/news/" (:id entry)))))

(def routes
  {:GET {:fn #'list-news}
   :POST {:fn #'create-news}
   "new" {:GET {:fn #'new-form}}
   [:news-id] {:GET {:fn #'show-news}}})
