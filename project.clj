(defproject octopress-scraper-to-ghost "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main octopress-scraper-to-ghost.core 

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [expectations "1.4.52"]
                 [enlive "1.1.5"]
                 [clj-time "0.6.0"]
                 [org.clojure/data.json "0.2.4"]
                 [clj-http "0.9.0"]
                 [local/remark "0.9.3"]]
   :repositories {"project" "file://repo"})
