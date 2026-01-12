package de.panda.hlcyFrame.Message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageBuilder {

    private final Map<Integer, List<ChatPart>> lines = new HashMap<>();
    private int currentLine = 0;

    public static Component createGradientMessage(String message, Color start, Color end) {
        int length = message.length();
        Component result = Component.empty();

        for (int i = 0; i < length; i++) {
            float ratio = (float) i / Math.max(1, (length - 1));

            int r = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int g = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int b = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));

            Component letter = Component.text(
                    String.valueOf(message.charAt(i)),
                    TextColor.color(r, g, b)
            );

            result = result.append(letter);
        }

        return result;
    }

    public MessageBuilder setHlcy() {
        Component gradient = createGradientMessage("Halcyon ", Color.CYAN, Color.MAGENTA)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);

        ChatPart part = new ChatPart(gradient, false, false, false);
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addCommandMessage(String msg, String cmd, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, true, false, false);
        part.setCmd(cmd);
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addHoverMessage(String msg, String hover, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, true, true, false);
        part.setHoverString(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(hover)
                : hover);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addSetInChatMessage(String msg, String setInChatString, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, false, false, true);
        part.setSetInChatString(setInChatString);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addMultiFunctionalMessage(String msg, String cmd, String hover, String setInChatString, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(msg.toLowerCase())
                : msg);

        boolean isCmd = cmd != null;
        boolean isHover = hover != null;
        boolean isSetInChat = setInChatString != null;

        ChatPart part = new ChatPart(base, isCmd, isHover, isSetInChat);

        if (isCmd) part.setCmd(cmd);
        if (isHover) part.setHoverString(otherFont ? SmallFontConverter.toLowerCaseSmallFont(hover) : hover);
        if (isSetInChat) part.setSetInChatString(setInChatString);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addMessage(String msg, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toLowerCaseSmallFont(msg.toLowerCase())
                : msg);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(new ChatPart(base, false, false, false));
        return this;
    }

    public MessageBuilder addMessage(String msg) {
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>())
                .add(new ChatPart(Component.text(msg), false, false, false));
        return this;
    }

    public MessageBuilder addMessage(Component msg) {
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>())
                .add(new ChatPart(msg, false, false, false));
        return this;
    }

    public MessageBuilder newLine() {
        currentLine++;
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>());
        return this;
    }

    public MessageBuilder sendPlayers(List<Player> players) {
        players.forEach(this::send);
        return this;
    }


    public MessageBuilder send(Player player) {
        for (var entry : lines.entrySet()) {

            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(ClickEvent.suggestCommand(part.getSetInChatString()));

                base = base.append(text);
            }

            player.sendMessage(base);
        }
        return this;
    }

    public String getAsString() {
        StringBuilder result = new StringBuilder();

        for (var entry : lines.entrySet()) {
            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(ClickEvent.suggestCommand(part.getSetInChatString()));

                base = base.append(text);
            }

            result.append(LegacyComponentSerializer.legacySection().serialize(base));
        }

        return result.toString();
    }

    public Component getAsComponent() {
        Component result = Component.empty();
        int current = 0;
        for (var entry : lines.entrySet()) {

            current++;
            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(ClickEvent.suggestCommand(part.getSetInChatString()));

                base = base.append(text);
            }
            result = result.append(base);
            if (current < lines.size()) result = result.appendNewline();
        }
        return result;
    }

    public MessageBuilder send(List<Player> players) {
        players.forEach(this::send);
        return this;
    }

    public MessageBuilder sendActionBar(Player player) {
        for (var entry : lines.entrySet()) {

            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(ClickEvent.suggestCommand(part.getSetInChatString()));

                base = base.append(text);
            }

            player.sendActionBar(base);
        }
        return this;
    }

    public MessageBuilder sendActionBar(List<Player> players) {
        players.forEach(this::sendActionBar);
        return this;
    }
}