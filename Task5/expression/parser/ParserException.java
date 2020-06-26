package expression.parser;

import expression.classes.exceptions.ExpressionException;

public class ParserException extends ExpressionException {
    public ParserException(int pos, String message) {
        super(Integer.toString(pos), message);
    }

}
