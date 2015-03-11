(ns siteclj.core
  (:require [compojure.core :refer :all]
            [compojure.handler :as handler]
            [org.httpkit.server :as ohs]
            [siteclj.views :as vs]
            [siteclj.registration.ctrl :as src]
            [siteclj.news.ctrl :as snc]
            [ring.middleware.resource :as rmr]
            [route-map :as rm]))

(defn ok [cnt]
  {:body  cnt
   :status 200})

(defn view [v]
  (fn [req]
    (ok (v req))))

(def router
  {:GET {:fn (view #'vs/main)}
   "about" {:GET {:fn (view #'vs/about)}}
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

(defn user-session-mw [h]
  (fn [{{sid :id} :session :as req}]
    (if sid
      (let [user (src/user-by-session sid)
            resp (h (assoc req :current-user user))
            sid (or (get-in resp [:session :id]) sid)]
        (if (or (:destroy_session resp) (nil? user))
          resp
          (assoc resp :session {:id sid})))
      (h req))))

(def app
  (-> dispatch
      (user-session-mw)
      (resolve-route)
      (handler/site)
      (rmr/wrap-resource "public")))


(defn start  []
  (def stop
    (ohs/run-server #'app  {:port 8080})))

(comment
  (start)
  (stop)
  (require '[vinyasa.pull :as vp])
  (vp/pull 'com.draines/postal))
