package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.exceptions.DivisionByZeroException;
import expression.classes.exceptions.ExpressionException;

import java.math.BigInteger;

public class BigIntegerOperator implements Operator<BigInteger> {
    @Override
    public BigInteger sum(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger sub(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger mul(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger div(BigInteger a, BigInteger b) {
        try {
            return a.divide(b);
        } catch (ArithmeticException e) {
            return BigInteger.ZERO;
        }

    }

    @Override
    public BigInteger min(BigInteger a, BigInteger b) {
        if (a.compareTo(b) > 0) {
            return b;
        } else {
            return a;
        }
    }

    @Override
    public BigInteger max(BigInteger a, BigInteger b) {
        if (a.compareTo(b) < 0) {
            return b;
        } else {
            return a;
        }
    }

    @Override
    public BigInteger neg(BigInteger a) {
        return a.negate();
    }

    @Override
    public BigInteger count(BigInteger a) {
        return new BigInteger(Integer.toString(a.bitCount()));
    }

    @Override
    public void throwSum(BigInteger a, BigInteger b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwSub(BigInteger a, BigInteger b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwMul(BigInteger a, BigInteger b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwDiv(BigInteger a, BigInteger b, TripleExpression expr) throws ExpressionException {
        if (b.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException(expr);
        }
    }

    @Override
    public void throwNeg(BigInteger a, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public BigInteger parse(String s) {
        return new BigInteger(s);
    }
}
