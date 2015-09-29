# Prometheus

A very rough Clojure wrapper around [Prometheus](http://prometheus.io/), an open-source service monitoring system and time series database.

## Usage

```clojure

(require '[prometheus.raw :as count])

(def counters {:some-count (count/mk-counter 
                            :application-name 
                            :some-count 
                            [] 
                            "text description")})

(defn inc-counter
  ([key]       (inc-counter key {}))
  ([key pairs] (apply (count/incrementer-fn counters telemetry/version) [key pairs])))
  
(inc-counter :some-count))  
```

## Future

Some idle thoughts about tidying the interface using watchers on atoms. None of this exists yet.

```clojure
(require '[promethesus.core :as prom])
(def errors (atom 0))
(prom/register-counter errors)
(swap! errors inc)

(def errors (atom 0))

(prom/start!)
;;  (inc-counter :failed-batches {:type worker-type :error-code (str (.getClass e))})

(def failed-batches (atom {:error-code 0}))
(prom/register-counter failed-batches)
(swap! failed-batches assoc :error-code (-> @failed-batches :error-code inc))

(prom/start!)
```

## License

Copyright © 2015 Joseph Wilk

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
