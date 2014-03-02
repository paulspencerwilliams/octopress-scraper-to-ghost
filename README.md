octopress-scraper-to-ghost
==========================

octopress-scraper-to-ghost aims to deliver a simple command line tool that will extract content from blogs published with [Octopress](http://octopress.org) into the [Ghost](http://ghost.org) [import format](https://github.com/tryghost/Ghost/wiki/import-format).

Usage
=====

Not sure yet, need to figure how to build clojure command line apps..

About
=====

I developed octopress-scraper-to-ghost to scratch an itch - I used to publish ['The Photographic Me'](http://thephotographic.me.uk) using Octopress but due to several issues, including me losing my git repository, meant that migrating from Octopress to Ghost wasn't as simple as using an existing plugin.

octopress-scraper-to-ghost uses [Remark](http://remark.overzealous.com/manual/) to parse html and generate markdown, and is distributed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
