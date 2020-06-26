package expression.generic;

import expression.classes.TripleExpression;
import expression.classes.exceptions.ExpressionException;
import expression.classes.operators.*;
import expression.parser.ExpressionParser;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private static final Map<String, Operator<? extends Number>> OPERATOR = Map.of(
            "i", new IntegerOperator(),
            "d", new DoubleOperator(),
            "bi", new BigIntegerOperator(),
            "l", new LongOperator(),
            "s", new ShortOperator(),
            "u", new UIntegerOperator()
    );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        return makeTable(OPERATOR.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T extends Number> Object[][][] makeTable(Operator<T> operator, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        ExpressionParser<T> parser = new ExpressionParser<>(operator);
        TripleExpression<T> tripleExpression = parser.parse(expression);
        Object[][][] ans = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int x = 0; x <= x2 - x1; x++) {
            for (int y = 0; y <= y2 - y1; y++) {
                for (int z = 0; z <= z2 - z1; z++) {
                    try {
                        ans[x][y][z] = tripleExpression.evaluate(operator.parse(Long.toString(x + x1)), operator.parse(Long.toString(y + y1)), operator.parse(Long.toString(z + z1)));
                    } catch (ExpressionException e) {
                        ans[x][y][z] = null;
                    }
                }
            }
        }
        return ans;
    }
}
