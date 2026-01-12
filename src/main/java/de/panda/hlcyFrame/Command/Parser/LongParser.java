package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class LongParser implements ArgumentParser<Long> {
    @Override
    public Long parse(String input) throws ArgumentParseException {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
        }
        return 0L;
    }

    @Override
    public boolean isValid(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    @Override
    public Type getType() {
        return Long.class;
    }
}
