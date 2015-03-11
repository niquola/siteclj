(ns siteclj.migrations
  (:require
    [siteclj.news.db :as snd]
    [siteclj.registration.db :as srd]))

(defn migrate []
  (snd/migrate)
  (srd/migrate))

(defn rollback []
  (snd/rollback)
  (srd/rollback))

(comment
  (rollback)
  (migrate))
