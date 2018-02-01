(ns transitions.utils
  (:require ["react-motion" :refer [Motion spring]]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.dom :as dom]
            [goog.object :as gobj]))

(defn factory-apply
  "A function for wrapping a js React class in a factory."
  [class]
  (fn [props & children]
    (apply js/React.createElement
      class
      props
      children)))

(def ui-motion (factory-apply Motion))

