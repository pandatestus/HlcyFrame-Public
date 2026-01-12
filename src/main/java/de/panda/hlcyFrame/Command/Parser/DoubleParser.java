package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class DoubleParser implements ArgumentParser<Double> {
    @Override
    public Double parse(String input) throws ArgumentParseException {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
        }
        return 0.0;
    }

    @Override
    public boolean isValid(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    @Override
    public Type getType() {
        return Double.class;
    }
}
