package expression.classes.exceptions;

import expression.classes.TripleExpression;

public class OverflowException extends ExpressionException {
    public OverflowException(TripleExpression expression) {
        super("overflow", expression.toMiniString());
    }
}
