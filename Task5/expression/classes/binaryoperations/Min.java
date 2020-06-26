package expression.classes.binaryoperations;

import expression.classes.*;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.Operator;

public class Min<T extends Number> extends BinaryOperation<T> {
    public Min(TripleExpression<T> el1, TripleExpression<T> el2, Operator<T> op) {
        super(el1, el2, "min", op);
    }

    @Override
    protected T calculate(T first, T second) {
        return op.min(first, second);
    }

    @Override
    protected void throwExceptions(T first, T second) throws ExpressionException {

    }

    @Override
    public int getPriority() {
        return 1;
    }
}
