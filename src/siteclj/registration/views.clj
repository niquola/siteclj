(ns siteclj.registration.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [hiccup.core :as hc]
    [siteclj.views :as sv]))

(defn sign-up [{member :member errors :errors :as req}]
  (let [input (sv/mk-input {:data member :errors errors})]
    (sv/html-layout
      req
      [:div.container
       [:div.row
        [:form.col.s12 {:action (sv/url "sign-up") :method "POST"}
         [:div.row
          [:h2 "Registration"]]
         [:div.row
          (for [[k v] errors]
            [:li [:b (str k)] (str v)])]
         [:div.row (input {:name :login :type :text})]
         [:div.row
          (input {:name :first_name :cols 6})
          (input {:name :last_name :cols 6})]
         [:div.row
          (input {:name :password :type :password :cols 6})
          (input {:name :password_confirmation :type :password :cols 6})]
         [:div.row
          (input {:name :phone :cols 6})
          (input {:name :email :type :email :cols 6})]
         [:div.row (input {:name :organization})]
         [:div.row
          [:button.waves-effect.waves-light.btn "Register"]]]]])))
