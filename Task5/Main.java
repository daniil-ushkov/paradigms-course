import expression.classes.TripleExpression;
import expression.classes.operators.IntegerOperator;
import expression.classes.operators.ShortOperator;
import expression.generic.GenericTabulator;
import expression.parser.ExpressionParser;

public class Main {
    public static void main(String[] args) {
        TripleExpression<Short> expression = new ExpressionParser<>(new ShortOperator()).parse("10 max 2 min 3");
        System.out.println(expression.evaluate((short) 2147483634, (short) 2147483643, (short) 2147483635));
        GenericTabulator genericTabulator = new GenericTabulator();
        genericTabulator.tabulate("i",
                "10000000 * x * y * 10000000 + z",
                -14, 4,
                -9, 7,
                -16, 17);
    }
}
