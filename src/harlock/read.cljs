(ns harlock.read
  (:require [clojure.tools.reader :as r]
            [clojure.tools.reader.reader-types :as rt]))

;; https://stackoverflow.com/a/30928487
(defn take-while+
  [pred coll]
  (lazy-seq
   (when-let [[f & r] (seq coll)]
     (if (pred f)
       (cons f (take-while+ pred r))
       [f]))))

;; reading succeeded if the last element is like:
;;
;; [:error
;;  #error {:message "[line 1, col 16] EOF while reading.",
;;          :data {:type :reader-exception,
;;                 :ex-kind :eof,
;;                 :file nil,
;;                 :line 1,
;;                 :col 16}}]
;;
;; so "[line <X>, col <Y>] EOF while reading." means success
;;
;; reading failed if the last element is like:
;;
;; [:error
;;  #error {:message "[line 1, col 14] Unexpected EOF while reading item 3 of list, starting at line 1 and column 9.",
;;          :data {:type :reader-exception,
;;                 :ex-kind :eof,
;;                 :file nil,
;;                 :line 1,
;;                 :col 14}}]
;;
;; so "[line <X>, col <Y>] Unexpected EOF while reading..." means failure
(defn form+strings-from-code-str
  [code-str]
  (let [rdr (rt/source-logging-push-back-reader code-str)
        parse-fn (fn [_]
                   (try
                     (r/read+string rdr)
                     (catch js/Object e
                       [:stopped e])))]
    (take-while+ (fn [[_ str-or-ex]]
                   (string? str-or-ex))
                (iterate parse-fn (parse-fn nil)))))

;; XXX: probably needs work
(defn ns-from-code-str
  [code-str]
  (let [rdr (rt/string-push-back-reader code-str)]
    (try
      (loop [form (r/read rdr)]
        (when form
          (let [head (first form)]
            (if (and (= (type head) cljs.core/Symbol)
                     (= (name head) "ns"))
              ;; XXX: might not work sometimes...
              (when-let [ns-name (second form)]
                (name ns-name))
              (recur (r/read rdr))))))
      (catch js/Object e
        ;; XXX
        nil))))
