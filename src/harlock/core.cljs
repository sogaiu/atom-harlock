(ns harlock.core
  (:require [harlock.atom :as ha]
            [harlock.config :as hc]
            [harlock.dispose :as hd]
            [harlock.term :as ht]))

(def config
  (hc/get-config))

(defn- install-deps-maybe
  []
  (-> (.install (js/require "atom-package-deps") "harlock")
      (.then #(ha/info-message "All dependencies installed"))))

(defn activate
  [s]
  (install-deps-maybe)
  (hd/reset-disposables!)
  (hc/observe-config!)  
  ;;
  (hd/command-for "connect-to-arcadia-socket-repl" ht/start-repl)
  (hd/command-for "disconnect" ht/stop-repl)
  (hd/command-for "send-line" ht/send-line)
  (hd/command-for "send-selection" ht/send-selection)
  (hd/command-for "send-block" ht/send-block)
  (hd/command-for "send-file" ht/send-file)
  (hd/command-for "load-file" ht/load-file!)
  (hd/command-for "switch-to-file-ns" ht/switch-to-file-ns))

(defn deactivate
  [s]
  (hd/dispose-disposables!))

(defn before
  [done]
  (deactivate nil)
  (done)
  (activate nil)
  (ha/info-message "Reloaded Harlock"))
