(ns prometheus.raw
  (:import [org.eclipse.jetty.server Server]
           [org.eclipse.jetty.servlet ServletHolder ServletContextHandler]
           [io.prometheus.client Prometheus]
           [io.prometheus.client.utility.servlet MetricsServlet]
           [io.prometheus.client.metrics Counter Counter$Partial]))

(defn mk-counter [namespace counter-name labels desc]
  (-> (Counter/newBuilder)
      (.namespace     (-> namespace name    (clojure.string/replace #"-" "_")))
      (.name          (-> counter-name name (clojure.string/replace #"-" "_")))
      (.labelNames    (into-array String (->> labels
                                              (map name)
                                              (map #(clojure.string/replace %1 #"-" "_")))))
      (.documentation desc)
      (.build)))

(defn incrementer-fn
  ([counters version] (incrementer-fn counters version 1))
  ([counters version inc-amount]
     (fn [counter-key label-pairs]
       (let [counter (get counters counter-key)
             counting (.newPartial counter)
             label-pairs (assoc label-pairs "server_version" version)]
         (doseq [[key value] label-pairs] (.labelPair counting (name key) (str value)))
         (-> counting (.apply) (.increment inc-amount))))))

(defn start! [port]
  (Prometheus/defaultInitialize)
  (let [server  (Server. ^Integer port)
        context (ServletContextHandler.)]
    (.setContextPath context "/")
    (.setHandler server context)
    (.addServlet context
                 (ServletHolder. (MetricsServlet.))
                 "/")
    (.start server)
    server))
