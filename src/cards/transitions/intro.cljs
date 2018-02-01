(ns transitions.intro
  (:require ["react-motion" :refer [Motion spring]]
            [devcards.core]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro-css.css :as css]
            [transitions.ui.components :as comp]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.dom :as dom]
            [goog.object :as gobj]
            [fulcro.client.mutations :as m]))

(defn factory-apply
  [class]
  (fn [props & children]
    (apply js/React.createElement
      class
      props
      children)))

(def ui-motion (factory-apply Motion))

(defsc Demo [this {:keys [ui/open?]} _ {:keys [demo demo-block]}]
  {:query         [:ui/open?]
   :initial-state {:ui/open? false}
   :css           [[:.demo {:border-radius    "4px"
                            :background-color "rgb(240, 240, 232)"
                            :position         "relative"
                            :margin           "5px 3px 10px"
                            :width            "450px"
                            :height           "50px"}]

                   [:.demo-block {:position         "absolute"
                                  :width            "50px"
                                  :height           "50px"
                                  :border-radius    "4px"
                                  :background-color "rgb(130, 181, 198)"}]]
   :ident         (fn [] [:control :demo])}
  (dom/div nil
    (dom/button #js {:onClick (fn [] (m/toggle! this :ui/open?))} "Toggle")
    (ui-motion (clj->js {:style {"x" (spring (if open? 400 0))}})
      (fn [p]
        (let [x (gobj/get p "x")]
          (prim/with-parent-context this
            (dom/div #js {:className demo}
              (dom/div #js {:className demo-block :style #js {:transform (str "translate3d(" x "px, 0, 0)")}}))))))))

(def ui-demo (prim/factory Demo))

(defsc Root [this {:keys [demo]}]
  {:query         [{:demo (prim/get-query Demo)}]
   :initial-state {:demo {}}}
  (dom/div nil
    (css/style-element Demo)
    (ui-demo demo)))

(defcard-fulcro SVGPlaceholder
  Root
  {}
  {:inspect-data true})
