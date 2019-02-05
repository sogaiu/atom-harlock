(ns harlock.promises)

(defn resolved-promise
 [body]
 (.resolve js/Promise body))

(defn new-promise
 [f]
 (js/Promise. f))

(defn then
 [p f]
 (.then p f))
