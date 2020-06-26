package expression.classes.unaryoperations;

import expression.classes.TripleExpression;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.Operator;

public class Count<T extends Number> extends UnaryOperation<T> {

    public Count(TripleExpression<T> expression, Operator<T> op) {
        super(expression, "-", op);
    }

    @Override
    protected T calculate(T value) {
        return op.count(value);
    }

    @Override
    protected void throwExceptions(T value) throws ExpressionException {

    }
}
