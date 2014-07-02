(ns cayley-clj.http
  (:require [http.async.client :as h]
            [clojure.data.json :as json]
            [clojure.walk :as w]))

(defn send
  [body url]
  (with-open [client (h/create-client)]
    (let [headers {"Content-Type" "text/json"}
          response (-> (h/POST client
                               url
                               :headers headers
                               :body body)
                       h/await)
          status (:code (h/status response))
          res-body (h/string response)]
      (if (= 200 status)
        (w/keywordize-keys (json/read-str res-body))
        (throw (ex-info "Error on query execute." {:error res-body
                                                   :http-context {:header headers
                                                                  :body body
                                                                  :http-status status
                                                                  :full-response response}}))))))
