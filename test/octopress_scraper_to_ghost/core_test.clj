(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]))

(def blog-url "http://thephotographic.me.uk")

(expect "ghost-json" 
        (jsonify-blog blog-url))

