(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]
        [clojure.data.json :as json]))

(def blog-url "http://thephotographic.me.uk")

(expect "ghost-json" 
        (jsonify-blog blog-url))

(spit "/Users/will/Desktop/output.json" (jsonify-blog blog-url))

