package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.exceptions.DivisionByZeroException;
import expression.classes.exceptions.ExpressionException;

public class ShortOperator implements Operator<Short> {

    @Override
    public Short sum(Short a, Short b) {
        return (short) (a + b);
    }

    @Override
    public Short sub(Short a, Short b) {
        return (short) (a - b);
    }

    @Override
    public Short mul(Short a, Short b) {
        return (short) (a * b);
    }

    @Override
    public Short div(Short a, Short b) {
        return (short) (a / b);
    }

    @Override
    public Short min(Short a, Short b) {
        return (short) Math.min(a, b);
    }

    @Override
    public Short max(Short a, Short b) {
        return (short) Math.max(a, b);
    }

    @Override
    public Short neg(Short a) {
        return (short) -a;
    }

    @Override
    public Short count(Short a) {
        return (short) Integer.bitCount(Short.toUnsignedInt(a));
    }

    @Override
    public void throwSum(Short a, Short b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwSub(Short a, Short b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwMul(Short a, Short b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwDiv(Short a, Short b, TripleExpression expr) throws ExpressionException {
        if (b == 0) {
            throw new DivisionByZeroException(expr);
        }
    }

    @Override
    public void throwNeg(Short a, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public Short parse(String s) {
        return (short) Long.parseLong(s);
    }
}
