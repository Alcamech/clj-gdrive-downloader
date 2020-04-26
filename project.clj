(defproject clj-gdrive-downloader "0.1.0"
  :description "Download shared files from Google Drive."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [clj-http "3.10.1"]
                 [hickory "0.7.1"]
                 [me.raynes/fs "1.4.6"]]
  :main ^:skip-aot clj-gdrive-downloader.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
