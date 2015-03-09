(ns siteclj.views
  (:require
    [hiccup.page :refer  (html5 include-css include-js)]
    [hiccup.core :as hc]))

(defn menu [req]
  [{:text "About" :href "/sign-up"}
   {:text "News" :href "/news"}
   {:text "Membership" :href "/sign-up"}])

(defn link-to [x]
  [:a {:href (:href x)} (:text x)])

(defn url [& parts]
  (apply str (map #(str "/" %) parts)))

(defn html-layout  [req content]
  (html5
    {:lang "en"}
    [:head
     [:title "fhirbase"]
     (include-css "//cdnjs.cloudflare.com/ajax/libs/materialize/0.95.3/css/materialize.min.css")
     [:body
      [:nav.light-blue.darken-4 {:role "navigation"}
       [:div.nav-wrapper.container
        [:a#logo-container.brand-logo
         {:href "/"} "HL7 Russia"]
        [:ul.right
         (for [l (menu req)]
           [:li (link-to l)])]]]
      [:div.wrap content]
      (include-js
        "https://code.jquery.com/jquery-1.11.2.min.js"
        "https://cdnjs.cloudflare.com/ajax/libs/materialize/0.95.3/js/materialize.min.js")]]))

(defn main [req]
  (html-layout
    req
    [:div.container
     [:h1]]))

(defn mk-input [opt]
  (fn [{nm :name tp :type cols :cols}]
    (let [nms (name nm)
          tp (or (and tp (name tp)) "text")
          vl (and (:data opt) (get (:data opt) nm))
          er (and (:errors opt) (get (:errors opt) nm))]
      [:div.input-field.col {:class (str "s" (or cols 12))}
       (if (= tp "textarea")
         [:textarea.materialize-textarea {:id nms :name nms :type tp :class (and er "invalid")}  vl]
         [:input.validate {:id nms :value vl :name nms :type tp :class (and er "invalid")}])
       [:label {:for nms} nms]
       (when er [:b (pr-str er)])])))
