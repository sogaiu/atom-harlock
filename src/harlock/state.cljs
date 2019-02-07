(ns harlock.state
  (:require [harlock.atom :as ha]))

(def eol
  (.-EOL (js/require "os")))

(def term-str-template
  {:darwin "nc -6 %s %s"
   :linux "rlwrap nc -6 %s %s"
   :win32 "ncat.exe -6 %s %s"})

(def config
  {:warn-at-terminal
   {:description "Display warning at terminal?"
    :type :boolean
    :default true}
   ;;
   :repl-host
   {:description "REPL host name"
    :type :string
    :default "localhost"}
   ;;
   :repl-port
   {:description "REPL port"
    :type :string
    :default "37220"}
   ;;
   :repl-cmd-format
   {:description "REPL connection command template"
    :type :string
    :default ((ha/current-platform) term-str-template)}})

(defn- seed-config []
  (->> config
       (map (fn [[k v]]
              [k (:default v)]))
       (into {})))

(def state
  (atom {:config (seed-config)}))

(defn make-darwin-warn-str
  [pre-warning warning template-str]
  (str "# " pre-warning eol
       "#" eol
       "# e.g. nc -- macos should come with this pre-installed" eol
       "#      recommended: rlwrap for convenient editing -- see homebrew" eol
       eol
       "# " warning eol
       template-str))  

(defn make-linux-warn-str
  [pre-warning warning template-str]
  (str "# " pre-warning eol
       "#" eol
       "# e.g. nc -- may need to install GNU netcat or OpenBSD netcat" eol
       "#      recommended: rlwrap for convenient editing" eol
       eol
       "# " warning eol
       template-str))

(defn make-win32-warn-str
  [pre-warning warning template-str]
  (str "# " pre-warning eol
       "#" eol
       "# e.g. ncat.exe should be in your PATH" eol
       "#      https://nmap.org/dist/ncat-portable-5.59BETA1.zip" eol
       "#      sha256 checksum:" eol
       "#      5e107ea10383110bd801fb7de11f59ee35f02b8e1defcadf34c0e3e769df9341"
       eol eol
       "# " warning eol
       template-str))

(defn make-term-str
  [platform]
  (let [template-str (get term-str-template platform)]
    (if (get-in @state [:config :warn-at-terminal])
      (let [pre-warning
            "Using external programs to connect to REPL"
            warning
            "press Enter if unity+arcadia is running and prerequisites met"]
        (case platform
          :darwin (make-darwin-warn-str pre-warning warning template-str)
          :linux (make-linux-warn-str pre-warning warning template-str)
          :win32 (make-win32-warn-str pre-warning warning template-str)))
      template-str)))
