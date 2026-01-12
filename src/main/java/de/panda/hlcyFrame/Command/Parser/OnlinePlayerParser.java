package de.panda.hlcyFrame.Command.Parser;

import de.panda.hlcyFrame.Exceptions.ArgumentParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class OnlinePlayerParser implements ArgumentParser<Player> {
    @Override
    public Player parse(String input) throws ArgumentParseException {
        return Bukkit.getPlayer(input);
    }

    @Override
    public boolean isValid(String input) {
        return Bukkit.getPlayer(input) != null;
    }

    @Override
    public Type getType() {
        return Player.class;
    }
}
