package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;

import java.lang.reflect.Type;

public class FloatParser implements ArgumentParser<Float>{
    @Override
    public Float parse(String input) throws ArgumentParseException {
        try {
            return Float.parseFloat(input);
        } catch (NumberFormatException e) {
        }
        return null;
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
        return Float.class;
    }
}
