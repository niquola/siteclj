(defproject siteclj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :source-paths  ["src" "embed/route-map/src"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [http-kit "2.1.16"]
                 [hiccup "1.0.5"]
                 [ring "1.3.2"]
                 [javax.servlet/servlet-api "2.5"]
                 [compojure "1.3.2"]
                 [prismatic/schema "0.4.0"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [org.postgresql/postgresql "9.3-1101-jdbc41"]
                 [markdown-clj "0.9.64"]
                 [honeysql "0.4.3"]
                 [clj-time "0.9.0"]
                 [clj-sql-up "0.3.5"]
                 [garden "1.2.5"]]
  :profiles
  {:dev {:plugins       [[lein-ancient "0.6.4"]]
         :dependencies  [[spyscope "0.1.4"]
                         [org.clojure/tools.namespace "0.2.4"]
                         [leiningen #=(leiningen.core.main/leiningen-version)]
                         [io.aviso/pretty "0.1.8"]
                         [im.chit/vinyasa "0.3.4"]]}})
