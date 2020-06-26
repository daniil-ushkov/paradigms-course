package expression.parser;

import expression.classes.TripleExpression;

public interface Operation<T extends Number> {
    TripleExpression<T> get(TripleExpression<T> ex1, TripleExpression<T> ex2);
}
