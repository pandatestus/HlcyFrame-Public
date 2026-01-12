package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public interface AsyncArgumentParser<T> {
    CompletableFuture<T> parseAsync(String input) throws ArgumentParseException;
    boolean isValid(String input);
    Type getType();
}