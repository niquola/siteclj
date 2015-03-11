(ns siteclj.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [garden.core :as gc]
    [hiccup.core :as hc]))

(defn menu [req]
  [{:text "Forum" :href "/news"}
   {:text "Marketplace" :href "/news"}
   ])

(defn link-to [x]
  [:a {:href (:href x)} (:text x)])

(defn url [& parts]
  (apply str (map #(str "/" %) parts)))

(defn html-layout  [req content]
  (html5
    {:lang "en"}
    [:head
     [:title "hin"]
     (include-css "//cdnjs.cloudflare.com/ajax/libs/materialize/0.95.3/css/materialize.min.css")
     [:body.grey.lighten-3
      [:nav {:role "navigation" :style "background-color: #222 !important;"}
       [:div.nav-wrapper.container
        [:a#logo-container.brand-logo
         {:href "/"}
         [:i.mdi-device-network-wifi]
         " hIN"]
        [:ul.right
         (for [l (menu req)]
           [:li (link-to l)])
         (when-let [user (:current-user req)]
           (list
             [:li [:a {:href (url "sign-up" "profile")} (:login user)]]
             [:li [:a {:href (url "sign-up" "sign-out")}
                   [:i.mdi-navigation-cancel]]]))]]]
      [:div.wrap content]
      (include-js
        "https://code.jquery.com/jquery-1.11.2.min.js"
        "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.95.3/js/materialize.min.js")]]))

(defn px [x]
  (str x "px"))

(def promo-style
  [:body
   [:.quote
    {:margin "30px"}
    [:p {:font-size "1.2em"}]]
   [:.splash
    {:color "white"
     :background-image "url(/imgs/splash.jpg)"
     :background-repeat "repeat"
     :opacity 0.8
     :padding (px 40)}
    [:h2 {:text-align "center" }]
    [:p  {:font-size "1.3em" :letter-spacing "0.5px"}]
    [:.login-box
     {:vertical-align "top"}
     ]
    [:.login-btn
     {:margin "1px"
      :display "inline-block"
      :box-sizing "border-box"
      :vertical-align "top"
      :width "350px"}]
    [:input.login
     {:background "white"
      :color "black"
      :height "52px"
      :font-size "18px"
      :box-shadow "rgba(0, 0, 0, 0.156863) 0px 2px 5px 0px, rgba(0, 0, 0, 0.117647) 0px 2px 10px 0px;"
      :padding (px 10)
      :margin "1px"
      :text-align "center"
      :border "none"
      :display "inline-block"
      :box-sizing "border-box"
      :width "350px"}]]])

(def people
  [{:name "Dr. Anthony Chang"
    :avatar "/avatars/chang.png"}
   {:name "Laura Beken"
    :avatar "/avatars/laura.jpg"}
   {:name "William West"
    :avatar "/avatars/william.jpg"}
   {:name "Vincent Serhan"
    :avatar "/avatars/vincent.jpg"}])

(defn people-card [p]
  [:div.col.s3
   [:div.card
    [:div.card-image.waves-effect.waves-block.waves-light
     [:img.activator {:src (:avatar p)}]]
    [:div.card-content
     [:div.activator.grey-text.text-darken-4 (:name p)
      [:i.mdi-navigation-more-vert.right]]
     #_[:span.card-title.activator.grey-text.text-darken-4.small (:name p)]]
    [:div.card-reveal
     [:span.card-title.grey-text.text-darken-4
      (:name p)
      [:i.mdi-navigation-close.right]]
     [:span.grey-text.text-darken-4 (:desc p)]]]])

(defn- sign-in-form []
  [:form {:action "/sign-up" :method "POST"}
   [:center
    [:div.login-box
     [:input.login {:type :email :name :email :placeholder "email"}]
     [:input.login {:type :password :name :password :placeholder "password"}]]
    [:div.login-box
     [:button.login-btn.waves-effect.waves-light.btn-large.red.darken-3 {:href "/sign-up"}
      [:i.mdi-action-label-outline] "Sign Up" ]
     [:button.login-btn.waves-effect.waves-light.btn-large.green.darken-4 {:href "/sign-up/sign-in"}
      [:i.mdi-action-label] "Sign In" ]]]])

(defn main [req]
  (html-layout
    req
    [:div
     [:style (gc/css promo-style)]
     [:div.splash.purple.darken-4
      [:div.container
       [:h2 "Healthcare Innovation Network"]
       [:p "A collaborative forum and a marketplace  that forms a direct pipeline between providers and the innovators of solutions designed to reduce costs and improve the quality of care"]
       [:br]
       (when (not (:current-user req)) (sign-in-form))]]
     [:div.container
      [:br]
      [:br]
      [:div.card
       [:div.card-content
        [:span.card-title {:style "color: black;"} "About Healthcare Innovation Network"]
        [:div.row
         [:div.col.s9
          [:div.quoute
           [:blockquote "The Healthcare Innovation Network exists to connect healthcare providers and innovators as equals, so that together that can better listen to each other and to patients who are now the center of the Innovation Universe and its ultimate customer."]
           [:blockquote "We hold these truths to be self evident, that healthcare providers, innovators and patients should be treated equal and are endowed with powers of curiosity and creativity in pursuit of better healthcare and happiness."]
           [:blockquote "Never forget that curiosity and fun are essential to innovation, which is defined as successful execution of creative ideas. Innovation in healthcare is impossible without cross silo collaboration, ability to effectively communicate your needs and be open to the ideas coming from outside!"]]]
         (people-card (second people))]]
       [:div.card-action
        [:a {:href "/about"} "Read more"]
        [:a {:href "/about"} "Contacts"]]]
      [:h3 "Who we are?"]
      [:div.row
       (for [p people] (people-card p))]]]))

(defn about [req]
  (html-layout
    req
    [:div.container
     [:div.card
      [:div.card-content
       [:h3 "About"]]]]))

(defn mk-input [opt]
  (fn [{nm :name tp :type cols :cols :as opts}]
    (let [nms (name nm)
          tp (or (and tp (name tp)) "text")
          vl (and (:data opt) (get (:data opt) nm))
          er (and (:errors opt) (get (:errors opt) nm))]
      [:div.input-field.col {:class (str "s" (or cols 12))}
       (if (= tp "textarea")
         [:textarea.materialize-textarea (merge opts {:id nms :name nms :type tp :class (and er "invalid")})  vl]
         [:input.validate (merge opts {:id nms :value vl :name nms :type tp :class (and er "invalid")})])
       [:label {:for nms} nms]
       (when er [:b (pr-str er)])])))

(defn card-form [{url :action meth :method title :title cnt :content btns :buttons}]
  [:div.container
   [:form.card {:action url :method meth}
    (into [:div.card-content
           (when title [:span.card-title {:style "color: black;" } title])] cnt)
    (when btns (into [:div.card-action] btns))]])

(defn errors-message [{status :status message :message errors :errors}]
  (when (= status :error)
    (list
      (when message
        [:div.row.red-text
         [:p.col.s12 message]])
      (when (not (empty? errors))
        [:div.row
         (for [[k v] errors]
           [:li [:b (str k)] (str v)])]))))
