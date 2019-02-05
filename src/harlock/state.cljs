(ns harlock.state
  (:require [harlock.atom :as ha]))

(def term-str-template
  {:darwin "nc %s %s"
   :linux "rlwrap nc %s %s"
   :win32 "ncat.exe %s %s"})

(def config
  {:warn-at-terminal
   {:description "Display warning at terminal?"
    :type :boolean
    :default true}
   ;;
   :repl-host
   {:description "REPL host name"
    :type :string
    :default "::1"}
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
  (str "# "
       pre-warning
       "\n"
       "# e.g. nc -- macos should come with this pre-installed"
       "\n"
       "# may also want rlwrap for convenient editing -- see homebrew"
       "\n"
       "\n"
       "# "
       warning
       "\n"
       template-str))  

(defn make-linux-warn-str
  [pre-warning warning template-str]
  (str "# "
       pre-warning
       "\n"
       "# e.g. nc -- GNU netcat or OpenBSD netcat, may need to install"
       "\n"
       "# may also want rlwrap for convenient editing"
       "\n"
       "\n"
       "# "
       warning
       "\n"
       template-str))

(defn make-win32-warn-str
  [pre-warning warning template-str]
  (str "REM "
       pre-warning
       "\n"
       "REM e.g. ncat.exe -- https://nmap.org/dist/ncat-portable-5.59BETA1.zip"
       "\n"
       "\n"
       "REM "
       warning
       "\n"
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
