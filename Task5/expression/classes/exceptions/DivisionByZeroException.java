package expression.classes.exceptions;

import expression.classes.TripleExpression;

public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException(TripleExpression expression) {
        super("division by zero", expression.toMiniString());
    }
}
