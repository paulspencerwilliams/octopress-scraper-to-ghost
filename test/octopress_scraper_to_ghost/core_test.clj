(ns octopress-scraper-to-ghost.core-test
  (:use expectations 
        [octopress-scraper-to-ghost.core :refer :all]))

(expect "paul Hello, World!" (foo "paul "))
