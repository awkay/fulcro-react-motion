(ns transitions.intro
  (:require ["react-motion" :refer [Motion spring]]
            [devcards.core :refer-macros [mkdn-pprint-source defcard-doc]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro-css.css :as css]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.dom :as dom]
            [goog.object :as gobj]
            [fulcro.client.mutations :as m]))

(defn factory-apply
  "A function for wrapping a js React class in a factory."
  [class]
  (fn [props & children]
    (apply js/React.createElement
      class
      props
      children)))

(def ui-motion (factory-apply Motion))

(defsc Block
  "This is a component so we can show that transitions can easily be
  applied even when the content is a data-driven component by passing
  the animation parameters through computed."
  [this {:keys [name]} {:keys [x]} {:keys [demo-block]}]
  {:query         [:id :name]
   :css           [[:.demo-block {:position         "absolute"
                                  :width            "50px"
                                  :height           "50px"
                                  :border-radius    "4px"
                                  :background-color "rgb(130, 181, 198)"}]]
   :initial-state {:name :param/name :id :param/id}
   :ident         [:child/by-id :id]}
  (dom/div #js {:className demo-block
                :style     #js {:transform (str "translate3d(" x "px, 0, 0)")}}
    (str name)))

(def ui-block (prim/factory Block))

(defsc Demo [this {:keys [ui/slid? block]} _ {:keys [demo]}]
  {:query         [:ui/slid? {:block (prim/get-query Block)}]
   :initial-state {:ui/slid? false :block {:id 1 :name "N"}}
   :css           [[:.demo {:border-radius    "4px"
                            :background-color "rgb(240, 240, 232)"
                            :position         "relative"
                            :margin           "5px 3px 10px"
                            :width            "450px"
                            :height           "50px"}]]
   :css-include   [Block]
   :ident         (fn [] [:control :demo])}
  (dom/div nil
    (dom/button #js {:onClick (fn [] (m/toggle! this :ui/slid?))} "Toggle")
    (ui-motion (clj->js {:style {"x" (spring (if slid? 400 0))}})
      (fn [p]
        (let [x (gobj/get p "x")]
          ; The binding wrapper ensures that internal fulcro bindings are held within the lambda
          (prim/with-parent-context this
            (dom/div #js {:className demo}
              ; Use computed to pass the change to the child component
              (ui-block (prim/computed block {:x x})))))))))

(def ui-demo (prim/factory Demo))

(defsc Root [this {:keys [demo]}]
  {:query         [{:demo (prim/get-query Demo)}]
   :initial-state {:demo {}}}
  (dom/div nil
    (css/style-element Demo)
    (ui-demo demo)))

(defcard-doc
  "An animation of left-right translation. See the comments and the react-motion docs for more details. This is
  a data-driven clone of their [simple transition demo](https://github.com/chenglou/react-motion/blob/master/demos/demo0-simple-transition/Demo.jsx).

  Instead of placing the CSS on the HTML, we co-located it using Fulcro CSS, and also split the UI into pieces to show how
  data could be passed through the data-driven layers of Fulcro."
  (mkdn-pprint-source factory-apply)
  (mkdn-pprint-source ui-motion)
  (mkdn-pprint-source Block)
  (mkdn-pprint-source ui-block)
  (mkdn-pprint-source Demo)
  (mkdn-pprint-source Root))

(defcard-fulcro slide-demo
  Root
  {}
  {:inspect-data true})
