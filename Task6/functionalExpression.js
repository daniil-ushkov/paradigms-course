"use strict";

//Abstract function
const op = (calc, ...other) => (...val) => {
    let args = [];
    for (let el of other) {
        args.push(el(...val));
    }
    return calc(...args);
};

const cnst = a => (...val) => a;
const variable = name => (...val) => val[VARIABLES_MAP.get(name)];

//Operations
const OPERATIONS = new Map;
function makeOp(name, calc){
    let func = (...args) => op(calc, ...args);
    func.argsNum = calc.length;
    OPERATIONS.set(name, func);
    return func;
}

//Arguments sum
function sum(...args) {
    let result = 0;
    for (let el of args) {
        result += el;
    }
    return result;
}

//Median of array
function med(arr) {
    let copy = arr.slice();
    copy.sort((a, b) => a - b);
    return copy[Math.floor(copy.length / 2)];
}

const iff = makeOp("iff", (x, y, z) => (x >= 0 ? y : z));

const add = makeOp("+", (x, y) => x + y);
const subtract = makeOp("-", (x, y) => x - y);
const multiply = makeOp("*", (x, y) => x * y);
const divide = makeOp("/", (x, y) => x / y);

const negate = makeOp("negate", x => -x);
const abs = makeOp("abs", x => Math.abs(x));
const sin = makeOp("sin", x => Math.sin(x));
const cos = makeOp("cos", x => Math.cos(x));
const avg5 = makeOp("avg5", (x1, x2, x3, x4, x5) =>
    sum(x1 / 5, x2 / 5, x3 / 5, x4 / 5, x5 / 5));
const med3 = makeOp("med3", (x, y, z) => med([x, y, z]));

//Constants
let CONSTANTS = new Map;
function makeCnst(name, val) {
    let func = cnst(val);
    CONSTANTS.set(name, func);
    return func;
}
const one = makeCnst("one", 1);
const two = makeCnst("two", 2);
const pi = makeCnst("pi", Math.PI);
const e = makeCnst("e", Math.E);

//Maps
const VARIABLES = ["x", "y", "z"];
const VARIABLES_SET = new Set(VARIABLES);
const VARIABLES_MAP = new Map();
for (let i = 0; i < VARIABLES.length; i++) {
    VARIABLES_MAP.set(VARIABLES[i], i);
}

//Parser
const parse = function(expr) {
    let pos = 0;
    let stack = [];
    const eof = () => (pos === expr.length);
    const skip = function () {
        while (!eof() && expr[pos] === " ") pos++;
    };
    const parseToken = function () {
        skip();
        let oldPos = pos;
        while (!eof() && expr[pos] !== " ") pos++;
        return expr.substring(oldPos, pos);
    };
    while (!eof()) {
        let token = parseToken();
        if (OPERATIONS.has(token)) {
            let op = OPERATIONS.get(token);
            stack.push(op(...stack.splice(stack.length - op.argsNum)));
            continue;
        }
        if (CONSTANTS.has(token)) {
            stack.push(CONSTANTS.get(token));
        } else if (VARIABLES_SET.has(token)) {
            stack.push(variable(token));
        } else {
            stack.push(cnst(parseInt(token)));
        }
    }
    return stack[0];
};

//Examples
// let expr = parse("1 2 +");
console.log();
// console.log(variable("x")(1));