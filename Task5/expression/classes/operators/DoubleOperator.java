package expression.classes.operators;

import expression.classes.TripleExpression;
import expression.classes.Utilities;
import expression.classes.exceptions.ExpressionException;

public class DoubleOperator implements Operator<Double> {

    @Override
    public Double sum(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double sub(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double mul(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double div(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double min(Double a, Double b) {
        return Math.min(a, b);
    }

    @Override
    public Double max(Double a, Double b) {
        return Math.max(a, b);
    }

    @Override
    public Double neg(Double a) {
        return -a;
    }

    @Override
    public Double count(Double a) {
        return (double) Long.bitCount(Double.doubleToLongBits(a));
    }

    @Override
    public void throwSum(Double a, Double b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwSub(Double a, Double b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwMul(Double a, Double b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwDiv(Double a, Double b, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public void throwNeg(Double a, TripleExpression expr) throws ExpressionException {

    }

    @Override
    public Double parse(String s) {
        return Double.parseDouble(s);
    }
}
