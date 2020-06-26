package expression.classes.binaryoperations;

import expression.classes.*;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.Operator;

public class Multiply<T extends Number> extends BinaryOperation<T> implements Assosiative {
    public Multiply(TripleExpression<T> el1, TripleExpression<T> el2, Operator<T> op) {
        super(el1, el2, "*", op);
    }

    @Override
    protected T calculate(T first, T second) {
        return op.mul(first, second);
    }

    @Override
    protected void throwExceptions(T first, T second) throws ExpressionException {
        op.throwMul(first, second, this);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
