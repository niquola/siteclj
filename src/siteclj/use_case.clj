(ns siteclj.use-case)

(defmacro defcase [nm & hs]
  `(def ~nm (-> ~@(reverse hs))))

(defn intercept-error [h f]
  (fn [data]
    (let [res (f data)]
      (if (= (:status res) :error)
        res
        (h res)))))
