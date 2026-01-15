package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.Command.Parser.ArgumentParser;
import de.panda.hlcyFrame.Command.Parser.ParsedValueList;
import de.panda.hlcyFrame.HlcyFrame;
import de.panda.hlcyFrame.Message.CoreMessage;
import de.panda.hlcyFrame.Message.MessageBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class HlcyCommand extends Command {

    private String usage;
    private String description;
    private Allowed_Sender allowedSender;
    private List<String> aliases = new ArrayList<>();
    private List<Permission> permissions = new ArrayList<>();
    private List<Permission> disallowed = new ArrayList<>();
    private Consumer<Executor> onExecute;
    private List<Argument> subCommands = new ArrayList<>();
    private Map<Integer, List<ArgumentParser<?>>> parserMap = new HashMap<>();
    private BiFunction<Player, String[], List<String>> tabCompleteFunction;

    public HlcyCommand(@NotNull String name) {
        super(name);
    }

    public HlcyCommand build() {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getServer());

            this.setAliases(aliases);
            this.setLabel(getName());
            this.setUsage(usage);
            this.setDescription(description);
            commandMap.register(getName(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        HlcyFrame.addCommand(this);
        return this;
    }

    public HlcyCommand addParser(int arg, ArgumentParser<?> parser) {
        parserMap.computeIfAbsent(arg, k -> new ArrayList<>()).add(parser);
        return this;
    }

    public Argument newArgument(String arg, ArgumentParser<?> parser) {
        Argument argument = new Argument(arg, parser).setCommand(this);
        subCommands.add(argument);
        return argument;
    }

    public Argument newArgument(String arg) {
        Argument argument = new Argument(arg).setCommand(this);
        subCommands.add(argument);
        return argument;
    }

    public HlcyCommand onExecute(Consumer<Executor> exec) {
        this.onExecute = exec;
        return this;
    }

    public @NonNull HlcyCommand usage(@NonNull String usage) {
        this.usage = usage;
        return this;
    }

    public @NonNull HlcyCommand description(@NonNull String desc) {
        this.description = desc;
        return this;
    }

    public HlcyCommand addAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    public HlcyCommand allowedSender(Allowed_Sender sender) {
        this.allowedSender = sender;
        return this;
    }

    public HlcyCommand addAllowedPermission(Permission permission) {
        permissions.add(permission);
        return this;
    }

    public HlcyCommand addDisallowedPermission(Permission permission) {
        disallowed.add(permission);
        return this;
    }

    public HlcyCommand onTabComplete(BiFunction<Player, String[], List<String>> function) {
        this.tabCompleteFunction = function;
        return this;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String @NotNull [] args) {
        try {
            Player player = null;
            if (sender instanceof Player p) {
                player = p;
                if (allowedSender == Allowed_Sender.CONSOLE) {
                    player.sendMessage(CoreMessage.getMessage("CONSOLE_ONLY", HlcyFrame.isOtherFont()));
                    return false;
                }
            } else if (allowedSender == Allowed_Sender.PLAYER) {
                sender.sendMessage(CoreMessage.getMessage("PLAYER_ONLY", HlcyFrame.isOtherFont()));
                return false;
            }
            Executor ex = new Executor(sender, player, args, s);
            boolean allowed = false;

            for (Permission p : permissions) if (ex.sender().hasPermission(p)) allowed = true;
            for (Permission p : disallowed) if (ex.sender().hasPermission(p)) allowed = false;
            if (!allowed && (player != null && !player.isOp())) {
                sender.sendMessage(CoreMessage.getMessage("MISSING_PERMISSION", HlcyFrame.isOtherFont()));
                return false;
            }

            int maxLength = Integer.MIN_VALUE;
            int minLength = Integer.MAX_VALUE;
            for (Argument a : subCommands) {
                if (a.getMaxDepth() > maxLength) maxLength = a.getMaxDepth();
                if (a.getMinDepth(minLength) < minLength) minLength = a.getMinDepth(minLength);

                Bukkit.getLogger().info(maxLength + "");
            }

            if (args.length != 0 && (args.length - 1 < minLength || args.length - 1 > maxLength)) {
                sender.sendMessage(CoreMessage.getMessage("WRONG_COMMAND_USAGE", HlcyFrame.isOtherFont()));

                return false;
            }

            if (onExecute != null) onExecute.accept(ex);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            new MessageBuilder().setHlcy().addMultiFunctionalMessage(CoreMessage.getMessageString("ERROR", HlcyFrame.isOtherFont()),null, "§c" + ex.getMessage(),null, HlcyFrame.isOtherFont()).send(sender);
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) throws IllegalArgumentException {
        List<String> result = new ArrayList<>();
        Player player = null;
        if (sender instanceof Player p) player = p;

        if (tabCompleteFunction != null && player != null) {
            result.addAll(tabCompleteFunction.apply(player, args));
        }

        String current = args[Math.max(args.length - 1, 0)];
        if (current.isBlank()) {
            return result.stream()
                    .sorted()
                    .toList();
        }
        return result.stream()
                .filter(s -> s.startsWith(current))
                .sorted()
                .toList();
    }

    public enum Allowed_Sender {
        CONSOLE,
        PLAYER,
        ALL
    }
}
