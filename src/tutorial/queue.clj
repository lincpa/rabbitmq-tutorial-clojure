(ns tutorial.queue
  (:use com.mefesto.wabbitmq))

(def con-info {:host "localhost" :username "idiscc" :password "1d15cc"})

(defn delete-queue
  [queue-name exchange-name]
    (with-broker {:host "localhost" :username "idiscc" :password "1d15cc"}
      (with-channel
        (if (not (= "" exchange-name)) (exchange-delete exchange-name))
        (queue-delete queue-name))))
  
(defn create-queue 
  ([] (queue-declare "test.queue"))
  ([name] (if (nil? name) (queue-declare) 
               (queue-declare name)))
  ([name durable] (queue-declare name durable)))

(defn create-exchange 
  ([] (exchange-declare "test.exchange" "fanout"))
  ([name] (exchange-declare name "fanout"))
  ([name type] (exchange-declare name type)))

(defn delete-hello-world-queue []
    (delete-queue "text.exchange" "test.queue"))
  
