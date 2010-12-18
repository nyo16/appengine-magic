(ns appengine-magic.core
  (:import com.google.apphosting.api.ApiProxy))


(defn in-appengine-interactive-mode? []
  (try
    (let [stack-trace (.getStackTrace (Thread/currentThread))]
      (some #(or (.contains (.toString %) "swank.core")
                 (.contains (.toString %) "clojure.main$repl"))
            stack-trace))
    (catch java.security.AccessControlException ace
      false)))


(defn open-resource-stream [resource-name]
  (-> (clojure.lang.RT/baseLoader) (.getResourceAsStream resource-name)))


(defn appengine-environment-type []
  (let [env-property (System/getProperty "com.google.appengine.runtime.environment")]
    (cond
     (nil? env-property) :interactive
     (= env-property "Development") :dev-appserver
     (= env-property "Production") :production)))


(defn appengine-app-id []
  (-> (ApiProxy/getCurrentEnvironment) .getAppId))


(defn appengine-app-version []
  (-> (ApiProxy/getCurrentEnvironment) .getVersionId))


(if (in-appengine-interactive-mode?)
    (load "core_local")
    (load "core_google"))
