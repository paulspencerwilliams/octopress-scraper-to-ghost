(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]))

(expect "ghost-json" 
        (get-ghost-json "http://thephotographic.me.uk/blog/archives"))
