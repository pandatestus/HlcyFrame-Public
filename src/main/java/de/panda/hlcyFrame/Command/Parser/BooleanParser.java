package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class BooleanParser implements ArgumentParser<Boolean> {
    @Override
    public Boolean parse(String input) throws ArgumentParseException {
        return input.equalsIgnoreCase("TRUE")
                || input.equalsIgnoreCase("YES")
                || input.equalsIgnoreCase("Y")
                || input.equals("1");
    }

    @Override
    public boolean isValid(String input) {
        return input.equalsIgnoreCase("TRUE") || input.equalsIgnoreCase("FALSE")
                || input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("NO")
                || input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("N")
                || input.equals("0") || input.equals("1");
    }

    @Override
    public Type getType() {
        return Boolean.class;
    }
}
