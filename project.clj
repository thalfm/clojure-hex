(defproject first-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.pedestal/pedestal.service "0.5.10"]
                 [io.pedestal/pedestal.route "0.5.10"]
                 [io.pedestal/pedestal.jetty "0.5.10"]
                 [org.clojure/data.json "2.4.0"]
                 [org.slf4j/slf4j-simple "2.0.5"]
                 [com.stuartsierra/component "1.1.0"]
                 #_[com.datomic/peer "1.0.7021"]
                 [yogthos/config "1.2.0"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [org.postgresql/postgresql "42.5.4"]
                 [com.github.seancorfield/honeysql "2.4.980"]
                 [hikari-cp "3.0.1"]
                 [prismatic/schema "1.4.1"]
                 [com.walmartlabs/lacinia "1.2.1"]
                 [com.walmartlabs/lacinia-pedestal "1.1"]
                 [io.aviso/logging "1.0"]
                 [prismatic/schema "1.4.1"]]
  :repl-options {:init-ns heroes-api.main}
  :main ^:skip-aot heroes-api.main
  :target-path "target/%s"
  :resource-paths ["config", "resources"]
  :profiles {:uberjar {:aot :all}})
