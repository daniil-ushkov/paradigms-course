package expression.classes.binaryoperations;

import expression.classes.*;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.Operator;

public class Divide<T extends Number> extends BinaryOperation<T> {
    public Divide(TripleExpression<T> el1, TripleExpression<T> el2, Operator<T> op) {
        super(el1, el2, "/", op);
    }

    @Override
    protected T calculate(T first, T second) {
        return op.div(first, second);
    }

    @Override
    protected void throwExceptions(T first, T second) throws ExpressionException {
        op.throwDiv(first, second, this);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
