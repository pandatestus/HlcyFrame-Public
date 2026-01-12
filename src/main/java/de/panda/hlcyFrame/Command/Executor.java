package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.Command.Parser.*;
import de.panda.hlcyFrame.HlcyFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Executor {

    private final CommandSender sender;
    private final Player player;
    private final String[] args;
    private final String cmd;

    public Executor(CommandSender sender, Player player, String[] args, String cmd) {
        this.sender = sender;
        this.player = player;
        this.args = args;
        this.cmd = cmd;
    }

    public CommandSender sender() {
        return sender;
    }

    public Player player() {
        return player;
    }

    public String[] args() {
        return args;
    }

    public String cmd() {
        return cmd;
    }

    public Integer[] integers() {
        IntegerParser parser = new IntegerParser();
        List<Integer> ints = new ArrayList<>();
        for (String arg : args) {
            if (parser.isValid(arg)) ints.add(parser.parse(arg));
            else ints.add(null);
        }

        return ints.toArray(Integer[]::new);
    }

    public Double[] doubles() {
        DoubleParser parser = new DoubleParser();
        List<Double> ints = new ArrayList<>();
        for (String arg : args) {
            if (parser.isValid(arg)) ints.add(parser.parse(arg));
            else ints.add(null);
        }

        return ints.toArray(Double[]::new);
    }


    public Long[] longs() {
        LongParser parser = new LongParser();
        List<Long> ints = new ArrayList<>();
        for (String arg : args) {
            if (parser.isValid(arg)) ints.add(parser.parse(arg));
            else ints.add(null);
        }

        return ints.toArray(Long[]::new);
    }


    public Boolean[] booleans() {
        BooleanParser parser = new BooleanParser();
        List<Boolean> ints = new ArrayList<>();
        for (String arg : args) {
            if (parser.isValid(arg)) ints.add(parser.parse(arg));
            else ints.add(null);
        }

        return ints.toArray(Boolean[]::new);
    }


    public Player[] onlinePlayers() {
        OnlinePlayerParser parser = new OnlinePlayerParser();
        List<Player> ints = new ArrayList<>();
        for (String arg : args) {
            if (parser.isValid(arg)) ints.add(parser.parse(arg));
            else ints.add(null);
        }

        return ints.toArray(Player[]::new);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] parse(Class<T> type) {
        ArgumentParser<T> tArgumentParser = (ArgumentParser<T>) HlcyFrame.getParser(type);
        List<T> parsedObjects = new ArrayList<>();
        for (String arg : args) {
            if (tArgumentParser.isValid(arg)) parsedObjects.add(tArgumentParser.parse(arg));
            else parsedObjects.add(null);
        }

        T[] array = (T[]) java.lang.reflect.Array.newInstance(type, parsedObjects.size());
        return parsedObjects.toArray(array);
    }

    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T>[] parseAsync(Class<T> type) {
        AsyncArgumentParser<T> tAsyncArgumentParser = (AsyncArgumentParser<T>) HlcyFrame.getAsyncParser(type);
        List<CompletableFuture<T>> parsedObjects = new ArrayList<>();
        for (String arg : args) {
            if (tAsyncArgumentParser.isValid(arg)) parsedObjects.add(tAsyncArgumentParser.parseAsync(arg));
            else parsedObjects.add(null);
        }

        CompletableFuture<T>[] array = (CompletableFuture<T>[]) new CompletableFuture[parsedObjects.size()];
        return parsedObjects.toArray(array);
    }

    public boolean hasIndex(int index) {
        return this.args != null && index >= 0 && index < this.args.length;
    }

    public String getText(int depth) {
        return String.join(" ", Arrays.copyOfRange(args, depth, args.length));
    }
}
