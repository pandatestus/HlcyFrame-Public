package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class StringParser implements ArgumentParser<String> {
    @Override
    public String parse(String input) throws IllegalArgumentException {
        return input;
    }

    @Override
    public boolean isValid(String input) {
        return true;
    }

    @Override
    public Type getType() {
        return String.class;
    }
}
