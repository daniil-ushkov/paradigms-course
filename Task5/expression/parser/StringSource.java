package expression.parser;

public class StringSource implements ExpressionSource {
    private final String data;
    private int pos;

    public StringSource(final String data) {
        this.data = data;
    }

    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public boolean hasPrev() {
        return pos > 0;
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }

    @Override
    public char prev() {
        if (pos == 0) {
            throw error("prev is out of bound");
        }
        return data.charAt(--pos - 1);
    }

    @Override
    public ParserException error(final String message) {
        return new ParserException(pos, message);
    }
}
