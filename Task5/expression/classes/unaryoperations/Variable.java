package expression.classes.unaryoperations;

import expression.classes.TripleExpression;
import expression.classes.exceptions.InvalidArgumentException;

public class Variable<T extends Number> implements TripleExpression<T> {
    private String symbol;

    public Variable(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        switch (symbol) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new InvalidArgumentException("invalid name of variable", this);
        }
    }

    @Override
    public String toString() {
        return symbol;
    }

    public String toMiniString() {
        return symbol;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
