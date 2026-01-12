package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class IntegerParser implements ArgumentParser<Integer> {
    @Override
    public Integer parse(String input) throws ArgumentParseException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
        }
        return 0;
    }

    @Override
    public boolean isValid(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    @Override
    public Type getType() {
        return Integer.class;
    }
}
