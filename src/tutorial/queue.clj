(ns tutorial.queue
  (:use com.mefesto.wabbitmq))

(def con-info {:host "localhost" :username "guest" :password "guest"})

(defn delete-queue
  [queue-name exchange-name]
    (with-broker con-info
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

  
