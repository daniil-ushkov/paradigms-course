package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.Utilities;
import expression.classes.exceptions.DivisionByZeroException;
import expression.classes.exceptions.ExpressionException;
import expression.classes.exceptions.OverflowException;

public class LongOperator implements Operator<Long> {

    @Override
    public Long sum(Long a, Long b) {
        return a + b;
    }

    @Override
    public Long sub(Long a, Long b) {
        return a - b;
    }

    @Override
    public Long mul(Long a, Long b) {
        return a * b;
    }

    @Override
    public Long div(Long a, Long b) {
        return a / b;
    }

    @Override
    public Long min(Long a, Long b) {
        return Math.min(a, b);
    }

    @Override
    public Long max(Long a, Long b) {
        return Math.max(a, b);
    }

    @Override
    public Long neg(Long a) {
        return -a;
    }

    @Override
    public Long count(Long a) {
        return (long) Long.bitCount(a);
    }

    @Override
    public void throwSum(Long a, Long b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwSub(Long a, Long b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwMul(Long a, Long b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwDiv(Long a, Long b, TripleExpression expr) throws ExpressionException {
        if (b == 0) {
            throw new DivisionByZeroException(expr);
        }
    }

    @Override
    public void throwNeg(Long a, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public Long parse(String s) {
        return Long.parseLong(s);
    }
}
