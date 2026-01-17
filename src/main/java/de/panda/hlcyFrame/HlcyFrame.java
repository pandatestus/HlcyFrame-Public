package de.panda.hlcyFrame;

import de.panda.hlcyFrame.Command.CommandInitializer;
import de.panda.hlcyFrame.Command.Help;
import de.panda.hlcyFrame.Command.HlcyCommand;
import de.panda.hlcyFrame.Command.Parser.*;
import de.panda.hlcyFrame.Test.TestCMD;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HlcyFrame extends JavaPlugin {

    @Getter
    @Setter
    private static boolean isOtherFont = true;
    @Getter
    private static Map<Type, ArgumentParser<?>> parserMap = new HashMap<>();
    @Getter
    private static Map<Type, AsyncArgumentParser<?>> asyncParserMap = new HashMap<>();
    @Getter
    private static List<HlcyCommand> commands = new ArrayList<>();

    @Setter
    @Getter
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        registerParser(Boolean.class, new BooleanParser());
        registerParser(Integer.class, new IntegerParser());
        registerParser(Double.class, new DoubleParser());
        registerParser(Long.class, new LongParser());
        registerParser(Player.class, new OnlinePlayerParser());
    }

    @Override
    public void onDisable() {

    }

    public static void registerParser(Type type, ArgumentParser<?> parser) {
        parserMap.put(type, parser);
    }

    public static void registerAsyncParser(Type type, AsyncArgumentParser<?> parser) {
        asyncParserMap.put(type, parser);
    }

    public static ArgumentParser<?> getParser(Type type) {
        return parserMap.get(type);
    }

    public static AsyncArgumentParser<?> getAsyncParser(Type type) {
        return asyncParserMap.get(type);
    }

    public static void addCommand(HlcyCommand command) {
        commands.add(command);
    }
}
