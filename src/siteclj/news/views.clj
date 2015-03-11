(ns siteclj.news.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [hiccup.core :as hc]
    [garden.core :as gc]
    [markdown.core :as mc]
    [siteclj.views :as sv]))

(defn px [x]
  (str x "px"))

(def style [])

(defn index [{news :news :as req}]
  (sv/html-layout
    req
    [:div.container
     [:style (gc/css style)]
     [:div.news
      (for [n news]
        [:div.card
         [:div.card-content
          [:span.card-title {:style "color: black;"} (:title n)]
          [:p (:abstract n)]]
         [:div.card-action
          [:a {:href (sv/url "news" (:id n))} "Read"]]])]
     [:div.fixed-action-btn {:style "bottom: 45px; right:45px; position: absolute;"}
      [:a.btn-floating.btn-large.red {:href "/news/new"}
       [:i.large.mdi-editor-mode-edit] ]]]))

(defn show [{news :news :as req}]
  (sv/html-layout
    req
    (list
      [:div.container
       [:div.card
        [:div.card-content
         [:span.card-title {:style "color: black;"}
          (:title news)]
         [:p (mc/md-to-html-string (:content news))]]
        [:div.card-action
         [:a {} "Like"]
         [:a {} "Recomend"]]]]
      (sv/card-form
        {:action (sv/url "news" (:id news)) :method "GET"
         :content [[:textarea {:style "border: 1px solid #ddd; min-height: 100px;" :placeholder "Comment"}]]}))))

(defn new-form [{form :form errors :errors :as req}]
  (let [input (sv/mk-input {:data form :errors errors})]
    (sv/html-layout
      req
      (sv/card-form
        {:action (sv/url "news") :method "POST"  :title "New post"
         :content
         [(sv/errors-message req)
          [:div.row (input {:name :title :type :text})]
          [:div.row (input {:name :abstract :type :text})]
          [:div.row (input {:name :content :type :textarea})]]
         :buttons [[:button.waves-effect.waves-light.btn "Publish"]
                   "&nbsp;"
                   "&nbsp;"
                   [:a {:href "/news"} "Cancel"]]}))))
