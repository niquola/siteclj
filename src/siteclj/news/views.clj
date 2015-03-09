(ns siteclj.news.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [hiccup.core :as hc]
    [garden.core :as gc]
    [markdown.core :as mc]
    [siteclj.views :as sv]))

(defn px [x]
  (str x "px"))

(def style
  [:.news-item
   {:color "black"
    :display "block"
    :border-bottom "1px solid #ddd"
    :padding (px 5)
    :margin (px 5)}])

(defn index [{news :news :as req}]
  (sv/html-layout
    req
    [:div.container
     [:style (gc/css style)]
     [:div.news
      (for [n news]
        [:a.news-item {:href (sv/url "news" (:id n))}
         [:h4 (:title n)]
         [:p (:abstract n)]])]
     [:div.fixed-action-btn {:style "bottom: 45px; right:45px; position: absolute;"}
      [:a.btn-floating.btn-large.red {:href "/news/new"}
       [:i.large.mdi-editor-mode-edit] ]]]))

(defn show [{news :news :as req}]
  (sv/html-layout
    req
    [:div.container
     [:h3 (:title news)]
     [:b (:updated_at news)]
     "&nbsp;"
     [:b (:created_at news)]
     [:p (mc/md-to-html-string (:content news))]]))

(defn new-form [{form :form errors :errors :as req}]
  (let [input (sv/mk-input {:data form :errors errors})]
    (sv/html-layout
      req
      [:div.container
       [:div.row
        [:form.col.s12 {:action (sv/url "news") :method "POST"}
         [:div.row
          (for [[k v] errors]
            [:li [:b (str k)] (str v)])]
         [:div.row (input {:name :title :type :text})]
         [:div.row (input {:name :abstract :type :text})]
         [:div.row (input {:name :content :type :textarea})]
         [:div.row
          [:button.waves-effect.waves-light.btn "Register"]]]]])))
