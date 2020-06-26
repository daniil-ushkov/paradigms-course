package expression.classes.unaryoperations;

import expression.classes.exceptions.ExpressionException;
import expression.classes.TripleExpression;
import expression.classes.binaryoperations.BinaryOperation;
import expression.classes.operators.Operator;

public abstract class UnaryOperation<T extends Number> implements TripleExpression<T> {
    private TripleExpression<T> expression;
    private String symbol;
    protected Operator<T> op;

    public UnaryOperation(TripleExpression<T> expression, String symbol, Operator<T> op) {
        this.expression = expression;
        this.symbol = symbol;
        this.op = op;
    }

    protected abstract T calculate(T value);

    protected abstract void throwExceptions(T value) throws ExpressionException;

    @Override
    public T evaluate(T x, T y, T z) {
        T value = expression.evaluate(x, y, z);
        throwExceptions(value);
        return calculate(value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(symbol);
        sb.append("(");
        sb.append(expression.toString());
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String toMiniString() {
        if (expression instanceof BinaryOperation) {
            StringBuilder sb = new StringBuilder(symbol);
            sb.append("(");
            sb.append(expression.toMiniString());
            sb.append(")");
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder(symbol);
            sb.append(expression.toMiniString());
            return sb.toString();
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

}
