(ns harlock.config
  (:require [clojure.walk :as walk]
            [harlock.dispose :as hd]
            [harlock.state :as hs]))

(defn- propagate-to-config
  [new-value]
  (let [config (. js/atom -config)]
    (doseq [[key value] (:config new-value)
            :let [atom-key (str "harlock." (name key))
                  atom-value (clj->js value)]
            :when (not= atom-value (.get config atom-key))]
      (.set config atom-key atom-value))))

(defn- propagate-to-state
  [new-value]
  (let [normalized (-> (js->clj new-value)
                       walk/keywordize-keys)]
    (when-not (= normalized (:config @hs/state))
      (swap! hs/state assoc
             :config normalized))))

(defn- transform-config
  [config]
  (let [type-for (fn [{:keys [type]}]
                   (if (vector? type)
                     :string
                     type))]
    (->> config
         (map (fn [[k v]]
                [k (cond-> {:type (type-for v)
                            :title (:description v)
                            :default (:default v)}
                     (vector? (:type v)) (assoc :enum (:type v)))]))
         (into {}))))

(defn get-config
  []
  (-> hs/config
      transform-config
      clj->js))

(defn observe-config!
  []
  (.add @hd/disposables
        (-> (.-config js/atom)
            (.observe "harlock" propagate-to-state)))
  (add-watch hs/state :config
             (fn [_ _ _ value]
               (propagate-to-config value))))
