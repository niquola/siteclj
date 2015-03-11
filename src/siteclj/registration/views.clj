(ns siteclj.registration.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [hiccup.core :as hc]
    [siteclj.views :as sv]))


(defn sign-up [{member :member errors :errors :as req}]
  (let [input (sv/mk-input {:data member :errors errors})]
    (sv/html-layout
      req
      (sv/card-form
        {:action (sv/url "sign-up"  "start") :method "POST" :title "Registration"
         :content
         [[:p "To start registration enter you email"]
          (sv/errors-message req)
          [:br]
          [:div (input {:name :email :type :email :required true})]]
         :buttons [[:button.waves-effect.waves-light.btn "Register"]]}))))

(defn key-emailed [{data :data errors :errors :as req}]
  (sv/html-layout
    req
    [:div.container
     [:br]
     [:div.card
      [:div.card-content
       [:span.card-title {:style "color: black;" }"Registration"]
       [:p "Activation link was emailed"]
       [:br]
       [:div
        [:a {:href (sv/url "sign-up" (:activation_key data))} "Open link"]]]
      [:div.card-action
       [:button.waves-effect.waves-light.btn "Resend"]]]]))

(defn fill-profile [{registration :registration member :member errors :errors :as req}]
  (let [input (sv/mk-input {:data member :errors errors})]
    (sv/html-layout
      req
      (sv/card-form
        {:action (sv/url "sign-up" (or (:activation_key member) (:activation_key registration))) :method "POST" :title "Fill profile details"
         :content
         [(sv/errors-message req)
          [:div.row (input {:name :login :type :text})]
          [:div.row
           (input {:name :password :type :password :cols 6 :required true})
           (input {:name :password_confirmation :type :password :cols 6 :required true})]
          [:div.row
           (input {:name :first_name :cols 6})
           (input {:name :last_name :cols 6})]
          [:div.row
           (input {:name :phone :cols 6})
           (input {:name :organization :cols 6})]]
         :buttons [[:button.waves-effect.waves-light.btn "Register"]]}))))

(defn profile [{usr :current-user :as req}]
  (sv/html-layout
    req [:div.container
         [:div.card
          [:div.card-content
           [:span.card-title {:style "color: black;"} "Your Profile"]
           [:ul
            (for [[k v] usr]
              [:li
               [:b (name k)] "&nbsp;" (pr-str v)])]]]]))
