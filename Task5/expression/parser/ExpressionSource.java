package expression.parser;

public interface ExpressionSource {
    boolean hasNext();
    boolean hasPrev();
    char next();
    char prev();
    ParserException error(final String message);
}
