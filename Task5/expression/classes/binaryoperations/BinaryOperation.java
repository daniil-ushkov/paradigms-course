package expression.classes.binaryoperations;

import expression.classes.exceptions.ExpressionException;
import expression.classes.TripleExpression;
import expression.classes.operators.Operator;

public abstract class BinaryOperation<T extends Number> implements TripleExpression<T> {
    private TripleExpression<T> ex1, ex2;
    private String symbol;
    protected Operator<T> op;

    BinaryOperation(TripleExpression<T> ex1, TripleExpression<T> ex2, String symbol, Operator<T> op) {
        this.ex1 = ex1;
        this.ex2 = ex2;
        this.symbol = symbol;
        this.op = op;
    }

    @Override
    public String toString() {
        StringBuilder ex = new StringBuilder();
        ex.append('(');
        ex.append(ex1.toString());
        ex.append(' ');
        ex.append(symbol);
        ex.append(' ');
        ex.append(ex2.toString());
        ex.append(')');
        return ex.toString();
    }

    private static StringBuilder wrap(StringBuilder expression) {
        StringBuilder wraped = new StringBuilder();
        wraped.append('(');
        wraped.append(expression);
        wraped.append(')');
        return wraped;
    }

    @Override
    public String toMiniString() {
        StringBuilder exp1 = new StringBuilder(ex1.toMiniString());
        StringBuilder exp2 = new StringBuilder(ex2.toMiniString());
        if (
                ex1.getPriority() > this.getPriority()
                && !(ex1 instanceof Divide)
            ) {
            exp1 = wrap(exp1);
        }
        if (
                ex2.getPriority() > this.getPriority()
                || (!(this instanceof Assosiative) && ex2 instanceof BinaryOperation && ex2.getPriority() == this.getPriority())
                || (this instanceof Multiply && ex2 instanceof Divide)
            ) {
            exp2 = wrap(exp2);
        }
        StringBuilder expression = new StringBuilder();
        expression.append(exp1);
        expression.append(' ');
        expression.append(symbol);
        expression.append(' ');
        expression.append(exp2);
        return expression.toString();
    }

    protected abstract T calculate(T first, T second);

    protected abstract void throwExceptions(T first, T second) throws ExpressionException;

    @Override
    public T evaluate(T x, T y, T z) {
        T first = ex1.evaluate(x, y, z);
        T second = ex2.evaluate(x, y, z);
        throwExceptions(first, second);
        return calculate(first, second);
    }
}
