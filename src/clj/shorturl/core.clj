(ns shorturl.core
  (:require
   [clojure.java.io :as io]
   [muuntaja.core :as m]
   [ring.util.response :as r]
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [ring.adapter.jetty :as ring-jetty])
  (:gen-class))

;; https://github.com/alndvz/vid4

(defn index []
  (slurp (io/resource "public/index.html")))

(defn create-redirect [req]
  (print req)
  (r/response {:status 200 :body "https://google.com"}))

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     ["api/"
      ["redirect/" {:post create-redirect}]]
     ["assets/*" (ring/create-resource-handler {:root "public/assets"})]
     ["" {:handler (fn [req] {:body (index) :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty #'app {:port  3001
                               :join? false}))

(defn -main []
  (println "starting app!")
  (start))

(comment
  (def server (start))
  (.stop server)
  )

