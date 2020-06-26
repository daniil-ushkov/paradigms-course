"use strict";

const BaseParser = function () {
    //Useful functions for parsing
    const isLetter = function (ch) {
        return ch.length === 1 && ch.match(/[a-z]/i);
    };
    const isDigit = function (ch) {
        return ch.length === 1 && ch.match(/[0-9]/i);
    };
    const isNumberBegin = (ch) => isDigit(ch) || ch === "-";
    const isIdentifierChar = function (ch) {
        return isLetter(ch) || isDigit(ch);
    };
    const isWhitespace = function (ch) {
        return ch === " ";
    };

    //Source constructor
    const source = {
        strExpr: undefined,
        pos: undefined,
        next: function () {
            return this.pos < this.strExpr.length ? this.strExpr[this.pos++] : "\0";
        }
    };

    function Source(strExpr) {
        this.strExpr = strExpr;
        this.pos = 0;
    }

    Source.prototype = source;

    //Parser abstract object, constructor and error
    //Each function except nextChar() which takes chars thinks what this.skip() was
    const parser = {
        source: undefined,
        ch: undefined,
        eof: function () {
            return this.ch === "\0";
        },
        skip: function () {
            while (!this.eof() && isWhitespace(this.ch)) this.nextChar();
        },
        nextChar: function () {
            this.ch = this.source.next();
        },
        parseTokenWhile: function (pred) {
            let token = "";
            while (!this.eof() && pred(this.ch)) {
                token += this.ch;
                this.nextChar();
            }
            this.skip();
            return token;
        },
        test: function (sym) {
            if (sym === this.ch) {
                this.nextChar();
                this.skip();
                return true;
            } else {
                return false;
            }
        },
        expect: function (sym) {
            if (this.ch === sym) {
                this.nextChar();
                this.skip();
            } else {
                throw this.error("'" + sym + "' expected");
            }
        },
        testCharPred: function (pred) {
            return pred(this.ch);
        },
        testStrPred: function (pred) {
            let oldPos = this.source.pos;
            let oldCh = this.ch;

            let token = this.parseTokenWhile((ch) => !isWhitespace(ch) && ch !== ")");

            this.source.pos = oldPos;
            this.ch = oldCh;
            return pred(token);
        },
        ParserError: function (pos, message) {
            this.message = pos + " : " + message;
            this.name = "ParserError";
        },
        error: function (message) {
            throw new this.ParserError(this.source.pos - 1, message)
        }
    };
    parser.ParserError.prototype = Error.prototype;

    function Parser(strExpr) {
        this.source = new Source(strExpr);
        this.nextChar();
    }

    Parser.prototype = parser;

    return {
        isDigit: isDigit,
        isNumberBegin: isNumberBegin,
        isLetter: isLetter,
        isIdentifierChar: isIdentifierChar,
        isWhitespace: isWhitespace,
        Source: Source,
        ParserError: parser.ParserError,
        Parser: Parser
    }
}();


const Arithmetic = function () {
    //Arithmetic operations
    const sum = (arr) => arr.reduce((a, b) => a + b, 0);
    const sumexp = (...args) => sum(args.map(el => Math.exp(el)));
    const softmax = (...args) => Math.exp(args[0]) / sumexp(...args);


    //Abstract objects
    const abstractConstOrVar = {
        value: undefined,
        toString: function () {
            return this.value.toString();
        },
        prefix: function () {
            return this.toString()
        },
        postfix: function () {
            return this.toString()
        }
    };

    const doToString = (bracket1, bracket2, args, toStrArgs) =>
        bracket1 + args.map(el => el[toStrArgs]()).join(" ") + bracket2;

    const abstractOp = {
        sym: undefined,
        calc: undefined,
        calcDiff: undefined,
        args: undefined,
        evaluate: function (...vars) {
            return this.calc.apply(this, this.args.map(el => el.evaluate(...vars)));
        },
        diff: function (variable) {
            return this.calcDiff(variable, ...this.args)
        },
        toString: function () {
            return doToString("", this.sym, this.args.reverse(), "toString")
        },
        prefix: function () {
            return doToString("(" + this.sym + " ", ")", this.args, "prefix")
        },
        postfix: function () {
            return doToString("(", " " + this.sym + ")", this.args, "postfix")
        },
    };

    function InvalidArgumentError() {
        this.name = "InvalidArgumentError";
        this.message = "wrong number of arguments";
    }

    InvalidArgumentError.prototype = Error.prototype;

    //Constructor creators
    function createConstOrVarConstructor(evaluate, diff) {
        let constructor = function (value) {
            this.value = value;
        };
        let constOrVar = Object.create(abstractConstOrVar);
        constOrVar.evaluate = evaluate;
        constOrVar.diff = diff;

        constructor.prototype = constOrVar;

        return constructor;
    }

    function createExprConstructor(sym, calc, calcDiff, limitedNumOfArgs = true) {
        let constructor = function (...args) {
            if (limitedNumOfArgs && args.length !== this.calc.length) {
                throw new InvalidArgumentError();
            }
            this.args = args;
        };

        let op = Object.create(abstractOp);
        op.sym = sym;
        op.calc = calc;
        op.calcDiff = calcDiff;

        constructor.prototype = op;
        constructor.argLen = calc.length;

        EXPR_CONSTR_BY_SYM.set(sym, constructor);
        EXPR_LEN_BY_CONSTR.set(constructor, limitedNumOfArgs ? calc.length : undefined);

        return constructor;
    }

    function createConst(name, value) {
        let constant = new Const(value);

        CONST_BY_SYM.set(name, constant);

        return constant;
    }

    //Variables names
    const VARIABLES = ["x", "y", "z"];
    const VARIABLES_MAP = new Map;
    for (let i = 0; i < VARIABLES.length; i++) {
        VARIABLES_MAP.set(VARIABLES[i], i);
    }

    //Maps for parsers
    const EXPR_CONSTR_BY_SYM = new Map;
    const EXPR_LEN_BY_CONSTR = new Map;
    const CONST_BY_SYM = new Map;


    //Constant and variable constructors
    const Const = createConstOrVarConstructor(
        function () {
            return this.value
        },
        () => new Const(0)
    );

    const Variable = createConstOrVarConstructor(
        function (...vars) {
            return vars[VARIABLES_MAP.get(this.value)]
        },
        function (name) {
            return name === this.value ? one : zero
        }
    );


    //Constructors for operations
    const Add = createExprConstructor(
        "+",
        (x, y) => x + y,
        (variable, expr1, expr2) => new Add(expr1.diff(variable), expr2.diff(variable)),
    );
    const Subtract = createExprConstructor(
        "-",
        (x, y) => x - y,
        (variable, expr1, expr2) => new Subtract(expr1.diff(variable), expr2.diff(variable))
    );
    const Multiply = createExprConstructor(
        "*",
        (x, y) => x * y,
        (variable, expr1, expr2) =>
            new Add(
                new Multiply(expr1.diff(variable), expr2),
                new Multiply(expr1, expr2.diff(variable))
            )
    );

    const Square = createExprConstructor(
        "sqr",
        x => x * x,
        (variable, expr) => new Multiply(two, new Multiply(expr, expr.diff(variable)))
    );

    const Divide = createExprConstructor(
        "/",
        (x, y) => x / y,
        (variable, expr1, expr2) =>
            new Divide(
                new Subtract(
                    new Multiply(expr1.diff(variable), expr2),
                    new Multiply(expr1, expr2.diff(variable))
                ),
                new Multiply(expr2, expr2)
            )
    );
    const Negate = createExprConstructor(
        "negate",
        x => -x,
        (variable, expr) => new Negate(expr.diff(variable))
    );
    let Log = createExprConstructor(
        "log",
        (x, y) => Math.log(Math.abs(y)) / Math.log(Math.abs(x)),
        (variable, expr1, expr2) =>
            new Divide(
                new Subtract(
                    new Multiply(
                        new Divide(expr2.diff(variable), expr2),
                        new Ln(expr1)
                    ),
                    new Multiply(
                        new Ln(expr2),
                        new Divide(expr1.diff(variable), expr1),
                    )
                ),
                new Square(
                    new Ln(expr1),
                )
            )
    );

    const Ln = createExprConstructor(
        "ln",
        x => Math.log(Math.abs(x)),
        (variable, expr) => new Divide(expr.diff(variable), expr)
    );

    const Power = createExprConstructor(
        "pow",
        (x, y) => Math.pow(x, y),
        function (variable, expr1, expr2) {
            let expr = new Multiply(expr2, new Ln(expr1));
            return new Multiply(
                this,
                expr.diff(variable)
            );
        }
    );

    const Exp = createExprConstructor(
        "exp",
        x => Math.exp(x),
        (variable, expr) => new Power(e, expr).diff(variable)
    );


    const Sum = createExprConstructor(
        "sum",
        (...args) => sum(args),
        (variable, ...exprs) => new Sum(...exprs.map(el => el.diff(variable))),
        false
    );

    const SumExp = createExprConstructor(
        "sumexp",
        sumexp,
        (variable, ...exprs) => (new Sum(...exprs.map(expr => new Exp(expr)))).diff(variable),
        false
    );

    const SoftMax = createExprConstructor(
        "softmax",
        softmax,
        (variable, ...exprs) => new Divide(new Exp(exprs[0]), new SumExp(...exprs)).diff(variable),
        false
    );


    //Constants
    const zero = createConst("zero", 0);
    const one = createConst("one", 1);
    const two = createConst("two", 2);
    const pi = createConst("pi", Math.PI);
    const e = createConst("e", Math.E);


    //Parser of reverse Polish notation,
    const parse = function (strExpr) {
        const parser = new BaseParser.Parser(strExpr);
        let stack = [];
        while (!parser.eof()) {
            parser.skip();
            let token = parser.parseTokenWhile((ch) => !BaseParser.isWhitespace(ch));
            if (EXPR_CONSTR_BY_SYM.has(token)) {
                let op = EXPR_CONSTR_BY_SYM.get(token);
                stack.push(new op(...stack.splice(stack.length - op.argLen)));
                continue;
            }
            if (CONST_BY_SYM.has(token)) {
                stack.push(CONST_BY_SYM.get(token));
            } else if (VARIABLES_MAP.has(token)) {
                stack.push(new Variable(token));
            } else {
                stack.push(new Const(parseInt(token)));
            }
        }
        return stack[0];
    };

    //Errors
    function createErrorConstructor(message) {
        function Constructor(pos) {
            BaseParser.ParserError.call(this, pos, message);
        }

        Constructor.prototype = BaseParser.ParserError.prototype;
        return Constructor;
    }

    const UnreachedEOFError = createErrorConstructor("end of expression was not reached");
    const UndefinedVarError = createErrorConstructor("undefined name of variable or constant");
    const InvalidTokenError = createErrorConstructor("invalid token");
    const OperationExpectedError = createErrorConstructor("operation expected");

    function assertAndReturn(pred, val, ParserError) {
        if (!pred) {
            throw new ParserError(this.source.pos - 1);
        }
        return val;
    }

    //PARSER BEGIN
    const abstractParser = BaseParser.Parser.prototype;
    abstractParser.parseOperation = function () {
        let token = this.parseTokenWhile(ch => !BaseParser.isWhitespace(ch) && ch !== "(" && ch !== ")");
        return assertAndReturn(EXPR_CONSTR_BY_SYM.has(token), EXPR_CONSTR_BY_SYM.get(token), OperationExpectedError);
    };
    abstractParser.parseArguments = function () {
        let args = [];
        while (!this.eof() && this.ch !== ")" && !this.testStrPred((str) => EXPR_CONSTR_BY_SYM.has(str))) {
            args.push(this.parseExpression());
        }
        return args;
    };
    abstractParser.parseExpression = function () {
        this.skip();
        if (this.test("(")) {
            try {
                let parsed = this.parseInside();
                this.expect(")");
                return parsed;
            } catch (e) {
                throw new (createErrorConstructor(e.message))(this.source.pos - 1);
            }
        }
        if (this.testCharPred(BaseParser.isLetter)) {
            let token = this.parseTokenWhile(BaseParser.isIdentifierChar);
            return assertAndReturn(VARIABLES_MAP.has(token), new Variable(token), UndefinedVarError);
        }
        return assertAndReturn(this.testCharPred(BaseParser.isNumberBegin),
            new Const(parseInt((this.test("-") ? "-" : "") + this.parseTokenWhile(BaseParser.isDigit))), InvalidTokenError);
    };
    abstractParser.parse = function () {
        let parsed = this.parseExpression();
        return assertAndReturn(this.eof(), parsed, UnreachedEOFError);
    };

    function createParserConstructor(parseInside) {
        function Constructor(expr) {
            this.source = new BaseParser.Source(expr);
            this.nextChar();
            this.parseInside = parseInside;
        }

        Constructor.prototype = abstractParser;
        return Constructor;
    }

    const parsePrefix = (expr) => new (createParserConstructor(function () {
        let op = this.parseOperation();
        let args = this.parseArguments();
        return new op(...args);
    }))(expr).parse();
    const parsePostfix = (expr) => new (createParserConstructor(function () {
        let args = this.parseArguments();
        let op = this.parseOperation();
        return new op(...args);
    }))(expr).parse();
    //PARSER END

    return {
        Const: Const,
        Variable: Variable,
        Add: Add,
        Subtract: Subtract,
        Multiply: Multiply,
        Square: Square,
        Divide: Divide,
        Log: Log,
        Ln: Ln,
        Power: Power,
        Exp: Exp,
        Negate: Negate,
        Sum: Sum,
        SumExp: SumExp,
        SoftMax: SoftMax,
        parse: parse,
        parsePrefix: parsePrefix,
        parsePostfix: parsePostfix,
    }
}();


//Renaming
const Const = Arithmetic.Const;
const Variable = Arithmetic.Variable;
const Add = Arithmetic.Add;
const Subtract = Arithmetic.Subtract;
const Multiply = Arithmetic.Multiply;
const Square = Arithmetic.Square;
const Divide = Arithmetic.Divide;
const Log = Arithmetic.Log;
const Ln = Arithmetic.Ln;
const Power = Arithmetic.Power;
const Negate = Arithmetic.Negate;
const Sumexp = Arithmetic.SumExp;
const Softmax = Arithmetic.SoftMax;
const parse = Arithmetic.parse;
const parsePrefix = Arithmetic.parsePrefix;
const parsePostfix = Arithmetic.parsePostfix;


//Tests
// let expr = parsePrefix("(+ 1 2)");
// console.log(expr.postfix());


