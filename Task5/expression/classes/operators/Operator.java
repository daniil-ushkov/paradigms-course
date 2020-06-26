package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.exceptions.ExpressionException;

public interface Operator<T> {
    T sum(T a, T b);
    T sub(T a, T b);
    T mul(T a, T b);
    T div(T a, T b);
    T min(T a, T b);
    T max(T a, T b);
    T neg(T a);
    T count(T a);

    void throwSum(T a, T b, TripleExpression expr) throws ExpressionException;
    void throwSub(T a, T b, TripleExpression expr) throws ExpressionException;
    void throwMul(T a, T b, TripleExpression expr) throws ExpressionException;
    void throwDiv(T a, T b, TripleExpression expr) throws ExpressionException;
    void throwNeg(T a, TripleExpression expr) throws ExpressionException;

    T parse(String s);
}
