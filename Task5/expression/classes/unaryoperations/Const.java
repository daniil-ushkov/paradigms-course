package expression.classes.unaryoperations;

import expression.classes.TripleExpression;

public class Const<T extends Number> implements TripleExpression<T> {
    private final T value;

    public Const(T value) {
        this.value = value;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String toMiniString() {
        return value.toString();
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
