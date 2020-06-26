(defn not-nils? [& objs] (every? false? (mapv nil? objs)))

(defn same-struct [& ts]
  {:pre [(apply not-nils? ts)]}
  (letfn [(bin-same-struct [t1 t2]
            (or
              (and (not (vector? t1)) (not (vector? t2)))
              (and (vector? t1) (vector? t2) (= (count t1) (count t2)) (every? true? (mapv bin-same-struct t1 t2)))
              ))] (every? true? (mapv bin-same-struct ts (next ts))))
  )
(defn element-op [check-type op]
  {:pre (not-nils? check-type op)}
  (fn [& ts]
    {:pre [(and (every? check-type ts) (apply same-struct ts))]
     :post (check-type %)}
    (letfn [(element-op' [& ts] (cond (every? number? ts) (apply op ts) :else (apply mapv element-op' ts)))]
      (apply element-op' ts)
      )
    )
  )


;Vector
(defn v? [v] {:pre [(not-nils? v)]} (and (vector? v) (every? number? v)))
(defn vector-of-val [val m] {:pre [(number? m)] :post (v? %)} (vec (for [i (range m)] val)))
(def vec-element-op (partial element-op v?))

(def v+ (vec-element-op +))
(def v- (vec-element-op -))
(def v* (vec-element-op *))

(defn v*s
  ([v & s]
   {:pre  [(v? v)]
    :post [(v? %)]}
   (mapv (partial * (reduce * 1 s)) v))
  )

(comment ":NOTE: just binary?")
(defn scalar [v1 v2] {:pre [(and (v? v1) (v? v2))]} (reduce + (v* v1 v2)))


;Matrix
(defn m? [m] {:pre [(not-nils? m)]} (and (vector? m) (every? v? m) (not (empty? m)) (apply = (mapv count m))))
(defn matrix-of-val [val n m] {:pre [(and (number? n) (number? m))] :post (m? %)} (vector-of-val (vector-of-val val m) n))
(comment ":NOTE: with `partial` it looks like copy-paste (like vec-element-op), but ok")
(def matrix-element-op (partial element-op m?))

(defn num-of-rows [m] {:pre [(m? m)]} (count m))
(defn num-of-cols [m] {:pre [(m? m)]} (count (nth m 0)))

(defn matched-matrices? [m1 m2] {:pre [(and (m? m1) (m? m2))]} (= (num-of-cols m1) (num-of-rows m2)))

;Vect
(defn minor2x2 [m i1 i2 j1 j2]
  {:pre [(m? m)]}
  (- (* (nth (nth m i1) j1) (nth (nth m i2) j2)) (* (nth (nth m i1) j2) (nth (nth m i2) j1))))
(defn vect [& vs]
  (let [
        bin-vect (fn [v1 v2]
                   {:pre  [(and (v? v1) (v? v2) (= (count v1) 3) (= (count v2) 3))]
                    :post [(v? %)]}
                   (let [m [v1 v2]] [(minor2x2 m 0 1 1 2) (- (minor2x2 m 0 1 0 2)) (minor2x2 m 0 1 0 1)])
                   )
        ] (reduce bin-vect vs))
  )

(def m+ (matrix-element-op +))
(def m- (matrix-element-op -))
(def m* (matrix-element-op *))
(defn m*s
  ([m & s]
   {:pre  [(m? m)]
    :post [(m? %)]}
   (comment ":NOTE: for what you need `iterate` here?")
   (mapv v*s m (iterate identity (reduce * 1 s))))
  )

(defn transpose [m]
  {:pre  [(m? m)]
   :post [(m? %)]}
  (apply mapv vector m))

(defn m*v [m v]
  {:pre  [(and (m? m) (v? v) (= (num-of-cols m) (count v)))]
   :post [(and (v? %) (= (num-of-rows m) (count %)))]}
  (mapv (partial scalar v) m))

(defn m*m [& m]
  (let [
        bin-m*m (fn [m1 m2]
                  {:pre  [(matched-matrices? m1 m2)]
                   :post [(m? %)]}
                  (mapv (partial m*v (transpose m2)) m1)
                  )
        ]
    (reduce bin-m*m m)
    )
  )


;Tensor
(defn t? [t] {:pre [(not-nils? t)]} (or (number? t) (and (vector? t) (apply same-struct t))))
(def tensor-element-op (partial element-op t?))

(def t+ (tensor-element-op +))
(def t- (tensor-element-op -))
(def t* (tensor-element-op *))