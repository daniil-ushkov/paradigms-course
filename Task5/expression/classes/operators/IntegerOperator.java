package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.Utilities;
import expression.classes.exceptions.DivisionByZeroException;
import expression.classes.exceptions.ExpressionException;
import expression.classes.exceptions.OverflowException;

public class IntegerOperator implements Operator<Integer> {

    @Override
    public Integer sum(Integer a, Integer b) {
        return a + b;
    }

    @Override
    public Integer sub(Integer a, Integer b) {
        return a - b;
    }

    @Override
    public Integer mul(Integer a, Integer b) {
        return a * b;
    }

    @Override
    public Integer div(Integer a, Integer b) {
        return a / b;
    }

    @Override
    public Integer min(Integer a, Integer b) {
        return Math.min(a, b);
    }

    @Override
    public Integer max(Integer a, Integer b) {
        return Math.max(a, b);
    }

    @Override
    public Integer neg(Integer a) {
        return -a;
    }

    @Override
    public Integer count(Integer a) {
        return Integer.bitCount(a);
    }

    @Override
    public void throwSum(Integer a, Integer b, TripleExpression expr) throws ExpressionException {
        if ((a >= 0 && b >= 0 && (a > Integer.MAX_VALUE - b))
                || (a <= 0 && b <= 0 && (a < Integer.MIN_VALUE - b))) {
            throw new OverflowException(expr);
        }
    }

    @Override
    public void throwSub(Integer a, Integer b, TripleExpression expr) throws ExpressionException {
        if ((a >= 0 && b <= 0 && (a > Integer.MAX_VALUE + b))
                || (a <= 0 && b >= 0 && (a < Integer.MIN_VALUE + b))) {
            throw new OverflowException(expr);
        }
    }

    @Override
    public void throwMul(Integer a, Integer b, TripleExpression expr) throws ExpressionException {
        try {
            Utilities.multiplyOverflowChecker(a, b);
        } catch (ExpressionException e) {
            throw new OverflowException(expr);
        }
    }

    @Override
    public void throwDiv(Integer a, Integer b, TripleExpression expr) throws ExpressionException {
        if (b == 0) {
            throw new DivisionByZeroException(expr);
        }
        if (a == Integer.MIN_VALUE && b == -1) {
            throw new OverflowException(expr);
        }
    }

    @Override
    public void throwNeg(Integer a, TripleExpression expr) throws ExpressionException {
        if (a == Integer.MIN_VALUE) {
            throw new OverflowException(expr);
        }
    }

    @Override
    public Integer parse(String s) {
        return Integer.parseInt(s);
    }
}
