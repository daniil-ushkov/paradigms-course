(comment "Base")
(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _show [result]
  (if (-valid? result) (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
                       "!"))
(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" input (_show (parser input)))) inputs))

(comment "Basic parsers")
(defn _empty [value] (partial -return value))
(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))
(defn _map [f result]
  (if (-valid? result)
    (-return (f (-value result)) (-tail result))))
(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        (_map (partial f (-value ar))
              ((force b) (-tail ar)))))))
(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))
(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(comment "Combinators")
(defn +char [chars] (_char (set chars)))
(defn +char-not [chars] (_char (comp not (set chars))))
(defn +map [f parser] (comp (partial _map f) parser))
(def +parser _parser)
(def +ignore (partial +map (constantly 'ignore)))
(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))
(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))
(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))
(defn +or [p & ps]
  (reduce _either p ps))
(defn +opt [p]
  (+or p (_empty nil)))
(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
(defn +plus [p] (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))

(comment "JS objects")
(defn proto-get [obj key]
  (cond
    (contains? obj key) (obj key)
    (contains? obj :prototype) (proto-get (obj :prototype) key)
    :else nil))

(defn proto-call [this key & args]
  (apply (proto-get this key) this args))

(defn field [key]
  (fn [this] (proto-get this key)))

(defn method [key]
  (fn [this & args] (apply proto-call this key args)))

(defn constructor [ctor prototype]
  (fn [& args] (apply ctor {:prototype prototype} args)))

(comment "TASK 10")
(defn median [& vals] (nth (sort vals) (quot (count vals) 2)))
(defn average [& vals] (cond (empty? vals) 0 :else (/ (apply + vals) (count vals))))

(def constant (fn [val] (constantly val)))
(def variable (fn [var] (fn [var-values] (get var-values var))))

(defn op [calc & exprs] (fn [var-values] (apply calc (mapv (fn [expr] (expr var-values)) exprs))))

(def add (partial op +))
(def subtract (partial op -))
(def multiply (partial op *))

(defn double-divide [& args] (reduce (fn [a b] (/ (double a) (double b))) args))
(defn divide [& exprs] (apply op double-divide exprs))

(defn negate [expr] (op - expr))
(def med (partial op median))
(def avg (partial op average))

(def func-ops-by-sym {'+ add '- subtract '* multiply '/ divide 'negate negate 'med med 'avg avg})

(defn parser-factory [op-map variable constant]
  (fn [expr-str]
    (letfn [(inner [arr]
              (apply (get op-map (first arr)) (mapv parse (rest arr))))
            (parse [expr]
              (if (and (coll? expr) (not (empty? expr)))
                (inner expr)
                (cond
                  (number? expr)
                  (constant expr)
                  :else
                  (variable (str expr))
                  )
                ))]
      (parse (read-string expr-str))
      )))


(def parseFunction (parser-factory func-ops-by-sym variable constant))

(comment "TASK 11")
(comment "Fields and methods")
(def sym (field :sym))
(def value (field :val))
(def args (field :args))
(def calc (field :calc))
(def calc-diff (field :calc-diff))
(def un? (field :un?))

(def evaluate (method :evaluate))
(def diff (method :diff))
(def toString (method :toString))
(def toStringSuffix (method :toStringSuffix))
(def toStringInfix (method :toStringInfix))

(defn vec-remove [pos coll] (vec (concat (subvec coll 0 pos) (subvec coll (inc pos)))))
(def calc-avg #(/ (apply + %&) (count %&)))

(comment "Constant and variable")
(defn const-or-var-factory [evaluate diff toString]
  (constructor
    (fn [this val] (assoc this :val val))
    {:evaluate evaluate :diff diff :toString toString :toStringSuffix toString :toStringInfix toString}))

(declare Zero)
(declare One)

(def Constant (const-or-var-factory
                (fn [this _] (value this))
                (fn [_ _] Zero)
                #(format "%.1f" (% :val))))
(def Variable (const-or-var-factory
                (fn [this vars] (vars (value this)))
                (fn [this var] (cond (= var (value this)) One :else Zero))
                #(str (% :val))))


(def Zero (Constant 0.0))
(def One (Constant 1.0))

(defn abstractToString [this fst-non-un sep-non-un pre-un post-un rec]
  (cond
    (un? this) (str pre-un "(" post-un (rec (first (args this))) ")")
    :else (str "(" fst-non-un (clojure.string/join sep-non-un (mapv rec (args this)))  ")")))
(comment "Operations")
(def proto-op
  {:evaluate      (fn [this vars] (apply (calc this) (mapv #(evaluate % vars) (args this))))
   :diff          (fn [this var] ((calc-diff this) (args this) (mapv #(diff % var) (args this))))
   :toString      #(abstractToString % (str (sym %) " ") " " "" (str (sym %) " ") toString)
   :toStringInfix #(abstractToString % "" (str " " (sym %) " ") (sym %) "" toStringInfix)})

(def op-constructor-factory-generator
  #(fn [sym calc calc-diff] (constructor % {:prototype proto-op :sym sym :calc calc :calc-diff calc-diff})))

(def op-constructor-factory (op-constructor-factory-generator #(assoc % :args %& :un? false)))
(def un-op-constructor-factory (op-constructor-factory-generator #(assoc %1 :args [%2] :un? true)))
(def bin-op-constructor-factory (op-constructor-factory-generator #(assoc %1 :args [%2 %3] :un? false)))


(def diff-linear #(fn [_ diff-args] (apply (force %) diff-args)))

(def Add (op-constructor-factory "+" + (diff-linear (delay Add))))
(def Subtract (op-constructor-factory "-" - (diff-linear (delay Subtract))))
(def Negate (un-op-constructor-factory "negate" - (diff-linear (delay Negate))))
(def Multiply (op-constructor-factory "*" *
                                      (fn [args diff-args]
                                        (apply Add (for [i (range (count args))]
                                                     (apply Multiply (nth diff-args i) (vec-remove i (vec args))))))))
(def Square #(Multiply % %))
(def Divide (op-constructor-factory "/" double-divide
                                    (fn [[num & denum-vec] [diff-num & diff-denum-vec]]
                                      (let [denum (apply Multiply denum-vec)
                                            diff-denum ((calc-diff denum) (args denum) diff-denum-vec)]
                                        (Divide (Subtract (Multiply diff-num denum) (Multiply num diff-denum))
                                                (Square denum))))))
(def Sum (op-constructor-factory "sum" + (diff-linear (delay Sum))))
(def Avg (op-constructor-factory "avg" calc-avg (fn [args diff-args] (Divide (apply Sum diff-args) (Constant (count args))))))

(def ln #(Math/log (Math/abs ^double %)))
(def log #(/ ^double (ln %2) ^double (ln %1)))
(def pow #(Math/pow %1 %2))

(def Ln (un-op-constructor-factory "ln" ln (constantly Zero)))
(def Log (bin-op-constructor-factory "//" log (constantly Zero)))
(def Pow (bin-op-constructor-factory "**" pow (constantly Zero)))

(def ops-by-sym
  {'+ Add '- Subtract 'negate Negate '* Multiply '/ Divide 'sum Sum 'avg Avg 'ln Ln (symbol "//") Log '** Pow})
(def parseObject (parser-factory ops-by-sym Variable Constant))

(comment "TASK 12")
(comment "Combine parser")
(comment "
Priorities:
  0: + -
  1: * /
  2: ** //
  3: un-op const var ()
")
(def fold-right
  "[ex0 [[op1 ex1] [op2 ex2] ...] -> [[[ex0 op1] [ex1 op2] ...] exn]"
  (comp #(conj (vector (mapv vector (take-nth 2 %) (take-nth 2 (next %)))) (last %)) #(cons (first %) (apply concat (second %)))))

(defn left [[fst-expr other]] (reduce (fn [acc [op expr]] (op acc expr)) fst-expr other))
(def right (comp (fn [[other last-expr]] (reduce (fn [acc [expr op]] (op expr acc)) last-expr (reverse other))) fold-right))

(def ops-by-prior {0 ['+ '-] 1 ['* '/] 2 ['ln (symbol "//") '**] 3 ['negate]})
(def bin-op-reducer {0 left 1 left 2 right})
(def last-prior (dec (count (keys ops-by-prior))))

(def *digits (+str (+plus (_char #(Character/isDigit ^char %)))))
(def *letter (_char #(Character/isLetter ^char %)))
(def *skip-ws #(+map first (+seq (+ignore (+star (+char " "))) % (+ignore (+star (+char " "))))))
(def *wrap #(*skip-ws (+map first (+seq (+ignore (+char "(")) (*skip-ws %) (+ignore (+char ")"))))))
(def *num (*skip-ws (+map read-string (+str (+seq (+opt (+char "-")) *digits (+char ".") *digits)))))
(def *token #(*skip-ws (apply +seqf str (mapv +char (mapv str %)))))
(def *ident (*skip-ws (+seqf str *letter (+str (+star (+or *letter *digits))))))
(def *const (*skip-ws (+map Constant *num)))
(def *var (*skip-ws (+map Variable *ident)))
(def *op #(*skip-ws (+map (partial get ops-by-sym) (+map symbol (*token (str %))))))
(def *op-prior #(apply +or (mapv *op (get ops-by-prior %))))

(declare *parse-prior)
(def *call-parse-prior #(delay (*parse-prior %)))
(def *un-op (+map (fn [[op content]] (op content)) (+seq (*op-prior 3) (*call-parse-prior 3))))
(def *parse-prior (memoize
                    #(cond
                       (= last-prior %) (+or *un-op *const *var (*wrap (*call-parse-prior 0)))
                       :else
                       (+map (get bin-op-reducer %)
                             (+seq (*call-parse-prior (inc %)) (+star (+seq (*op-prior %) (*call-parse-prior (inc %)))))))))

(def parseObjectInfix (+parser (*call-parse-prior 0)))
