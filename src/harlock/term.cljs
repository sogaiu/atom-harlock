(ns harlock.term
  (:require [clojure.string :as cs]
            [goog.object :as gobj]
            [goog.string :as gstr]
            ;; https://clojurescript.org/reference/ \
            ;;         google-closure-library#requiring-a-function
            [goog.string.format] ; see url above and find 'Sometimes...'
            [harlock.atom :as ha]
            [harlock.state :as hs]))

(def ait-name "atom-ide-terminal")

;; https://discuss.atom.io/t/call-atom-command-from-package/7465/4
;;   atom.commands.dispatch(atom.views.getView(atom.workspace), "name:command");
(defn ait-command
  [cmd-name]
  (.dispatch (.-commands js/atom)
             (ha/current-workspace)
             cmd-name))

(defn new-terminal
  []
  (ait-command (str ait-name ":new")))

(defn close-terminal
  []
  (ait-command (str ait-name ":close")))

(defn active-terminal
  []
  (let [^js ait-main (gobj/get (ha/get-active-pkg "atom-ide-terminal")
                               "mainModule")]
    (-> ait-main
        .-statusBarTile
        .-activeTerminal)))

(defn send-text
  ([txt]
   (send-text txt true))
  ([txt send-nl]
   (some-> (active-terminal)
          (.input (str txt (when send-nl "\n"))))))

(defn start-repl
  []
  (let [{:keys [repl-host repl-port]}
        (select-keys (:config @hs/state)
                     [:repl-host :repl-port])]
    (new-terminal)
    ;; XXX: ugly..
    (js/setTimeout #(send-text (gstr/format (hs/make-term-str
                                             (ha/current-platform))
                                            repl-host repl-port)
                               nil)
                   1000)))

(defn stop-repl
  []
  ;; XXX: is there a nice way to send control-c or something?
  (close-terminal))

(defn send-line
  []
  (send-text (ha/current-line)))

(defn send-selection
  []
  (send-text (ha/current-selection)))

(defn send-file
  []
  (send-text (ha/current-buffer)))

(defn load-file!
  []
  (send-text (str "(load-file \""
                  (ha/current-path)
                  "\")")))

(defn switch-to-file-ns
  []
  (let [target-ns (ha/current-ns)]
    (if target-ns
      (send-text (str "(in-ns '"
                      target-ns
                      ")"))
      (ha/info-message "Failed to determine ns"))))

(def repl-init
  (quote
   (binding [*warn-on-reflection* false]
     (do
       (println
        (str "\n"
             "; Arcadia REPL"
             "\n"
             "; Clojure " (clojure-version)
             "\n"
             "; Unity "
             (UnityEditorInternal.InternalEditorUtility/GetFullUnityVersion)
             "\n"
             "; Mono "
             (.Invoke
              (.GetMethod
               Mono.Runtime
               "GetDisplayName"
               (enum-or System.Reflection.BindingFlags/NonPublic
                        System.Reflection.BindingFlags/Static))
              nil
              nil)))))))

(defn send-block
  []
  (send-text
   (ha/current-block)))
