package de.panda.hlcyFrame.Command.Parser;

import java.lang.reflect.Type;
import java.util.*;

public class ParsedValueList<T> {
    private ArgumentParser<T> parser;
    private Type type;
    private final Map<Integer, T> values = new HashMap<>();

    public ParsedValueList(ArgumentParser<T> parser) {
        this.parser = parser;
    }

    public ParsedValueList() {
    }

    public void add(String input, int arg) {
        if (parser.isValid(input)) values.put(arg, parser.parse(input));
    }

    public void addValue(T value, int arg) {
        values.put(arg, value);
    }

    public Map<Integer, T> getValues() {
        return values;
    }

    public T getValue(int pos) {
        return values.get(pos);
    }

    public ArgumentParser<T> getParser() {
        return parser;
    }
}