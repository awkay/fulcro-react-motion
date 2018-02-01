(ns transitions.draggable-balls
  (:require [devcards.core :refer-macros [mkdn-pprint-source defcard-doc]]
            ["react-motion" :refer [spring]]
            [transitions.utils :refer [ui-motion]]
            [fulcro.client.cards :refer [defcard-fulcro]]
            [fulcro-css.css :as css]
            [fulcro.client.primitives :as prim :refer [defsc]]
            [fulcro.client.dom :as dom]
            [goog.object :as gobj]
            [goog.math :as gmath]
            [fulcro.client.mutations :as m]))


(def spring-setting-1 #js {:stiffness 180 :damping 10})
(def spring-setting-2 #js {:stiffness 120 :damping 17})

(def all-colors ["#EF767A" "#456990" "#49BEAA" "#49DCB1" "#EEB868" "#EF767A"
                 "#456990" "#49BEAA" "#49DCB1" "#EEB868" "#EF767A"])

(def ball-count 11)
(def width 70)
(def height 90)
(def layout (mapv (fn [n]
                    (let [row (js/Math.floor (/ n 3))
                          col (mod n 3)]
                      [(* width col) (* height row)])) (range ball-count)))

(def clamp gmath/clamp)
(def floor js/Math.floor)

(defn reinsert [old-order from to]
  (let [v            (nth old-order from)
        without-from (concat (take from old-order) (drop (inc from) old-order))
        with-to      (concat (take to without-from) [v] (drop to without-from))]
    (vec with-to)))

(defn handle-mouse-up [component evt]
  (prim/update-state! component assoc
    :pressed? false
    :mouse-delta [0 0]))

(defn handle-mouse-down [component key [pressX pressY] evt]
  (let [pageX (gobj/get evt "pageX")
        pageY (gobj/get evt "pageY")]
    (prim/update-state! component assoc
      :last-press key
      :pressed? true
      :mouse-delta [(- pageX pressX) (- pageY pressY)]
      :mouse-position [pressX pressY])))

(defn handle-mouse-move [component evt]
  (let [pageX (gobj/get evt "pageX")
        pageY (gobj/get evt "pageY")
        {:keys [order last-press pressed? mouse-delta]} (prim/get-state component)
        [dx dy] mouse-delta]
    (when pressed?
      (let [x        (- pageX dx)
            y        (- pageY dy)
            xy       [x y]
            col      (clamp (-> (floor x) (/ width)) 0 2)
            row      (clamp (-> (floor y) (/ height)) 0 (floor (-> ball-count (/ 3))))
            index    (-> row (* 3) (+ col))
            newOrder (reinsert order (.indexOf order last-press) index)]
        (prim/update-state! component assoc :mouse-position xy :order newOrder)))))

(defn- spring-constant [x]
  (-> (- x (-> (* 3 width)
             (- 50)
             (/ 2)))
    (/ 15)))

(defn compute-pressed-values [{[x y] :mouse-position}]
  {:x     x
   :y     y
   :style {:translate-x x
           :translate-y y
           :scale       (spring 1.2 spring-setting-1)
           :box-shadow  (spring (spring-constant x) spring-setting-1)}})

(defn compute-unpressed-values [visual-position {[x y] :mouse-position}]
  (let [[x y] (nth layout visual-position)]
    {:x     x
     :y     y
     :style {:translate-x (spring x spring-setting-2)
             :translate-y (spring y spring-setting-2)
             :scale       (spring 1 spring-setting-1)
             :box-shadow  (spring (spring-constant x) spring-setting-1)}}))

(defsc Demo [this _ _ {:keys [demo demo-ball outer]}]
  {:initLocalState (fn [] {:mouse-position [0 0]
                           :mouse-delta    [0 0]
                           :last-press     nil
                           :pressed?       false
                           :order          (vec (range ball-count))})
   :css            [[:.demo {:width "190px" :height "320px"}]
                    [:.outer {:height           "800px"
                              :width            "800px"
                              :position         "absolute"
                              :background-color "#EEE"
                              :display          "flex"
                              :justify-content  "center"
                              :align-items      "center"}]
                    [:.demo-ball {:position      "absolute"
                                  :border        "1px solid black"
                                  :border-radius "99px"
                                  :width         "50px"
                                  :height        "50px"}]]}
  (let [{:keys [pressed? last-press order] :as st} (prim/get-state this)]
    (dom/div #js {:className   outer
                  :onMouseMove #(handle-mouse-move this %)
                  :onMouseUp   #(handle-mouse-up this %)}
      (dom/div #js {:className demo}
        (map-indexed
          (fn [key v]
            (let [visual-position (.indexOf order key)
                  {:keys [x y style]} (if (and (= key last-press) pressed?)
                                        (compute-pressed-values st)
                                        (compute-unpressed-values visual-position st))]
              (ui-motion (clj->js {:key key :style style})
                (fn [js-style]
                  (let [{:keys [translate-x translate-y scale box-shadow]} (js->clj js-style :keywordize-keys true)]
                    (dom/div #js {:onMouseDown (partial handle-mouse-down this key [x y])
                                  :className   demo-ball
                                  :style       (clj->js {:backgroundColor (nth all-colors key)
                                                         :transform       (str "translate3d(" translate-x "px, " translate-y "px, 0) scale(" scale ")")
                                                         :zIndex          (if (= key last-press) 99 visual-position)
                                                         :boxShadow       (str box-shadow "px 5px 5px rgba(0,0,0,0.5)")})}))))))
          order)))))

(def ui-demo (prim/factory Demo))

(defsc Root [this props]
  (dom/div nil
    (css/style-element Demo)
    (ui-demo {})))

(defcard-doc
  "# Draggable Balls

  This is a Fulcro implementation of the [draggable balls](https://github.com/chenglou/react-motion/blob/master/demos/demo2-draggable-balls/Demo.jsx) demo from react-motion.
  The CSS is co-located with the components, and we made a few minor changes to make it more compatible with devcards:

  - Moved the event handles off of window and onto a large div
  - Made it render on a large, but explicitly-sized div instead of trying for 100% (since we're in a card already)

  A little refactoring was done to make it a bit more readable as well.

  For the full source see github. The significant things are:
  "
  (mkdn-pprint-source handle-mouse-up)
  (mkdn-pprint-source handle-mouse-down)
  (mkdn-pprint-source handle-mouse-move)
  (mkdn-pprint-source spring-constant)
  (mkdn-pprint-source compute-pressed-values)
  (mkdn-pprint-source compute-unpressed-values)
  (mkdn-pprint-source Demo))

(defcard-fulcro draggable-balls-demo
  Root
  {}
  {:inspect-data true})
