(ns siteclj.mail
  (:require [postal.core :as pc]))

(defn mail-to [ml]
  (pc/send-message
    {:from "niquola@gmail.com"
     :to  ["niquola@gmail.com"]
     :subject "Hi!"
     :body "Test."}))
