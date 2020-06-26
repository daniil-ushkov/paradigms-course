package expression.parser;

import expression.classes.exceptions.ExpressionException;
import expression.classes.TripleExpression;

public interface Parser {
    TripleExpression parse(String expression) throws ExpressionException;
}
