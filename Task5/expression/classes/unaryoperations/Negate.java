package expression.classes.unaryoperations;

import expression.classes.TripleExpression;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.Operator;

public class Negate<T extends Number> extends UnaryOperation<T> {

    public Negate(TripleExpression<T> expression, Operator<T> op) {
        super(expression, "-", op);
    }

    @Override
    protected T calculate(T value) {
        return op.neg(value);
    }

    @Override
    protected void throwExceptions(T value) throws ExpressionException {
        op.throwNeg(value, this);
    }
}
