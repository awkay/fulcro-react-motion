(defproject transitions "0.1.0-SNAPSHOT"
  :description "My Cool Project"
  :license {:name "MIT" :url "https://opensource.org/licenses/MIT"}
  :min-lein-version "2.7.0"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [fulcrologic/fulcro "2.1.5-SNAPSHOT"]
                 [fulcrologic/fulcro-css "2.0.0"]
                 [fulcrologic/fulcro-spec "2.0.0-beta3" :scope "test" :exclusions [fulcrologic/fulcro]]]

  :source-paths ["src/cards"]

  :profiles {:cljs {:source-paths ["src/cards"]
                    :dependencies [[binaryage/devtools "0.9.8"]
                                   [thheller/shadow-cljs "2.0.150"]
                                   [fulcrologic/fulcro-inspect "2.0.0-alpha5"]
                                   [devcards "0.2.4" :exclusions [cljsjs/react cljsjs/react-dom]]]}})
