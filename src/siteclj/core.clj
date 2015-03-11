(ns siteclj.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :as ohs]
            [siteclj.views :as vs]
            [siteclj.registration.ctrl :as src]
            [siteclj.news.ctrl :as snc]
            [route-map :as rm]))

(defn ok [cnt]
  {:body  cnt
   :status 200})

(defn index  [{params :params :as req}]
  (ok (vs/main req)))

(def router
  {:GET {:fn #'index}
   "news" snc/routes
   "sign-up" src/routes })

(defn match-route  [meth path]
  (rm/match  [meth path] router))

(defn resolve-route  [h]
  (fn  [{uri :uri meth :request-method :as req}]
    (if-let  [route  (match-route meth uri)]
      (h  (assoc req :route route))
      {:status 404 :body  (str "No route " meth " " uri " params:" (:params req))})))

(defn dispatch  [{handler :handler route :route :as req}]
  (let  [handler  (get-in route  [:match :fn])
         req      (update-in req  [:params] merge  (:params route))]
    (println "\n\nDispatching "  (:request-method req) " "  (:uri req) " to "  (pr-str handler))
    (handler req)))

(def app
  (-> dispatch
      (resolve-route)
      (handler/site)))

(defn start  []
  (def stop
    (ohs/run-server #'app  {:port 8080})))

(comment
  (start)
  (stop)
  (require '[vinyasa.pull :as vp])
  (vp/pull 'com.draines/postal)

  )
