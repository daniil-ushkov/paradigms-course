package expression.parser;

import expression.classes.*;
import expression.classes.binaryoperations.*;
import expression.classes.operators.Operator;
import expression.classes.unaryoperations.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionParser<T extends Number> implements Parser {
    private static Map<String, Integer> PRIORITIES = new LinkedHashMap<>();
    static {
        PRIORITIES.put("min", 1);
        PRIORITIES.put("max", 1);
        PRIORITIES.put("**", 4);
        PRIORITIES.put("//", 4);
        PRIORITIES.put("<<", 1);
        PRIORITIES.put(">>", 1);
        PRIORITIES.put("+", 2);
        PRIORITIES.put("-", 2);
        PRIORITIES.put("*", 3);
        PRIORITIES.put("/", 3);
    }

    private Operator<T> operator;
    private Map<String, Operation<T>> EXPRESSIONS = Map.of(
            "+", ((ex1, ex2) -> new Add<>(ex1, ex2, operator)),
            "-", ((ex1, ex2) -> new Subtract<>(ex1, ex2, operator)),
            "*", ((ex1, ex2) -> new Multiply<>(ex1, ex2, operator)),
            "/", ((ex1, ex2) -> new Divide<>(ex1, ex2, operator)),
            "min", ((ex1, ex2) -> new Min<>(ex1, ex2, operator)),
            "max", ((ex1, ex2) -> new Max<>(ex1, ex2, operator))
    );



    public ExpressionParser(Operator<T> operator) {
        this.operator = operator;
    }

    @Override
    public TripleExpression<T> parse(final String expression) {
        return parse(new StringSource(expression));
    }

    private TripleExpression<T> parse(ExpressionSource source) {
        return new SimpleParser(source).parseExpression('\0');
    }

    private class SimpleParser extends BaseParser {



        SimpleParser(ExpressionSource source) {
            super(source);
            nextChar();
        }

        private TripleExpression<T> parseExpression(char stop) {
            skipWhitespace();
            TripleExpression<T> operand = parseOperand(stop);
            TripleExpression<T> parsed = parseBinaryOperation(operand, 1, stop);
            if (test(stop)) {
                return parsed;
            } else {
                if (stop == ')') {
                    throw error("Unclosed bracket");
                } else if (softTest(')')) {
                    throw error("Unopened bracket");
                } else {
                    throw error("Binary expression expected");
                }
            }
        }

        private TripleExpression<T> parseOperand(final char stop) {
            skipWhitespace();
            if (test('(')) {
                return parseBrackets();
            } else if (between('0', '9')) {
                return parseNumber(false);
            } else if (test('-')) {
                if (between('0', '9')) {
                    return parseNumber(true);
                } else {
                    skipWhitespace();
                    return new Negate<>(parseOperand(stop), operator);
                }
            } else if (Character.isJavaIdentifierStart(ch)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isJavaIdentifierPart(ch) && ch != stop && ch != '\0') {
                    sb.append(ch);
                    nextChar();
                }
                String word = sb.toString();
                switch (word) {
                    case "count":
                        return new Count<>(parseOperand(stop), operator);
//                    case "abs":
//                        return new Abs(parseOperand(stop));
//                    case "square":
//                        return new Square(parseOperand(stop));
//                    case "log2":
//                        return new Log2(parseOperand(stop));
//                    case "pow2":
//                        return new Pow2(parseOperand(stop));
                    case "x":
                    case "y":
                    case "z":
                        return new Variable<>(word);
                }
                throw error("Illegal name of variable");
            } else {
                throw error("Operand expected");
            }
        }

        private String getSymbol() {
            for (Map.Entry<String, Integer> entry : PRIORITIES.entrySet()) {
                if (softTest(entry.getKey())) {
                    return entry.getKey();
                }
            }
            return null;
        }

        private TripleExpression<T> parseBinaryOperation(TripleExpression<T> begin, int priority, char stop) {
            skipWhitespace();
            String symbol = getSymbol();
            if (symbol == null || priority > PRIORITIES.get(symbol)) {
                return begin;
            }
            test(symbol);
            return parseBinaryOperation(
                    EXPRESSIONS.get(symbol).get(
                            begin,
                            parseBinaryOperation(
                                    parseOperand(stop),
                                    PRIORITIES.get(symbol) + 1,
                                    stop
                            )
                    ),
                    priority,
                    stop
            );
        }

        private TripleExpression<T> parseBrackets() {
            return parseExpression(')');
        }

        private TripleExpression<T> parseNumber(boolean negative) throws ParserException {
            StringBuilder number = new StringBuilder();
            while (between('0', '9')) {
                number.append(ch);
                nextChar();
            }
            TripleExpression<T> expression;
            try {
                if (negative) {
                    expression = new Const<>(operator.parse("-" + number.toString()));
                } else {
                    expression = new Const<>(operator.parse(number.toString()));
                }
            } catch (NumberFormatException e) {
                throw error("Illegal number");
            }
            return expression;
        }

        private void skipWhitespace() {
            while (test(' ') || test('\r') || test('\n') || test('\t'));
        }
    }
}
