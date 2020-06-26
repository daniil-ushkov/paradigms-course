package expression.classes.exceptions;

import expression.classes.TripleExpression;

public class InvalidArgumentException extends ExpressionException {
    public InvalidArgumentException(String type, TripleExpression expression) {
        super(type, expression.toMiniString());
    }
}
