(ns harlock.atom
  (:require [clojure.string :as cs]
            [harlock.read :as hr]))

(def platform-strs
  ["platform-darwin" "platform-linux" "platform-win32"])

(defn current-platform
  []
  (let [clist (.-classList (.-body js/document))]
    (loop [[plat & plats] platform-strs]
      (if (.contains clist plat)
        (keyword (subs plat
                       (inc (cs/index-of plat "-"))))
        (recur plats)))))

;; thanks to atom-packages-dependencies
(defn path-for-pkg
  [pkg-name]
  (->> pkg-name
       (.getLoadedPackage (.-packages js/atom))
       (.-mainModulePath)))

(defn get-active-pkg
  [pkg-name]
  (.getActivePackage (.-packages js/atom)
                     pkg-name))

(defn current-workspace
  []
  (.getView (.-views js/atom)
            (.-workspace js/atom)))

(defn warn-message
  ([title]
   (warn-message title ""))
  ([title text]
   (-> (.-notifications js/atom)
       (.addWarning title #js {:detail text}))))

(defn error-message
  ([title]
   (error-message title ""))
  ([title text]
   (-> (.-notifications js/atom)
       (.addError title #js {:detail text}))))

(defn info-message
  ([title]
   (info-message title ""))
  ([title text]
   (-> (.-notifications js/atom)
       (.addInfo title #js {:detail text}))))

(defn current-editor []
  (-> (.-workspace js/atom)
      .getActiveTextEditor))

(defn current-pos
  ([]
   (current-pos (current-editor)))
  ([^js editor]
   (let [point (.getCursorBufferPosition editor)]
     [(.-row point) (.-column point)])))

(defn current-line-pos
  ([]
   (current-line-pos (current-editor)))
  ([^js editor]
   (let [[row _] (current-pos editor)]
     row)))

(defn line-at
  ([line-no]
   (line-at (current-editor) line-no))
  ([^js editor line-no]
   (.lineTextForBufferRow editor line-no)))

(defn current-line
  ([]
   (current-line (current-editor)))
  ([^js editor]
   (.lineTextForBufferRow editor (current-line-pos editor))))

(defn current-col-pos
  ([]
   (current-col-pos (current-editor)))
  ([^js editor]
   (let [[_ col] (current-pos editor)]
     col)))

(defn current-selection
  ([]
   (current-selection (current-editor)))
  ([^js editor]
   (.getSelectedText editor)))

(defn current-buffer
  ([]
   (current-buffer (current-editor)))
  ([^js editor]
   (.getText editor)))

(defn current-path
  ([]
   (current-path (current-editor)))
  ([^js editor]
   (.getPath editor)))

;; XXX: probably needs work
(defn current-ns
  ([]
   (current-ns (current-editor)))
  ([^js editor]
   (hr/ns-from-code-str (current-buffer))))

(defn is-empty-or-whitespace
  [txt]
  (= (cs/trim txt) ""))

(defn is-comment
  [line-no]
  (let [firstChar (subs (cs/trim (line-at line-no))
                        0 1)]
    (= firstChar #";")))

;; XXX: blocks are non-empty lines bounded by empty lines
(defn current-block
  []
  (let [start-line-pos
        (last
         (take-while
          (fn [n]
            (not (is-empty-or-whitespace (line-at n))))
          (take-while (partial < -1)
                      (iterate dec (current-line-pos)))))
        end-lines
        (filter
         (comp not is-comment)
         (take-while
          (fn [line]
            (not (is-empty-or-whitespace line)))
          (map (fn [n]
                 (line-at n))
               (iterate inc
                        (if (is-empty-or-whitespace
                             (line-at start-line-pos))
                          (+ start-line-pos 1)
                          start-line-pos)))))]
    (cs/join "\n" end-lines)))
