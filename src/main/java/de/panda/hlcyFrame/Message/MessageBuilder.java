package de.panda.hlcyFrame.Message;

import de.panda.hlcyCorePublic.Chat.ChatPart;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;

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

    public static final String TITLE_FROM = "#5ec8ff";
    public static final String TITLE_TO   = "#9b7bff";
    public static final Color TITLE_FROM_COLOR = Color.decode(TITLE_FROM);
    public static final Color TITLE_TO_COLOR   = Color.decode(TITLE_TO);

    public MessageBuilder setHlcy() {
        Component gradient = createGradientMessage("Halcyon ", TITLE_FROM_COLOR, TITLE_TO_COLOR)
                .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD);

        ChatPart part = new ChatPart(gradient, false, false, false);
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }


    public MessageBuilder addCommandMessage(String msg, String cmd, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, true, false, false);
        part.setCmd(cmd);
        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addHoverMessage(String msg, String hover, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, true, true, false);
        part.setHoverString(otherFont
                ? SmallFontConverter.toSmallFont(hover)
                : hover);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addSetInChatMessage(String msg, String setInChatString, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, false, false, true);
        part.setSetInChatString(setInChatString);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addMultiFunctionalMessage(String msg, String cmd, String hover, String setInChatString, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        boolean isCmd = cmd != null;
        boolean isHover = hover != null;
        boolean isSetInChat = setInChatString != null;

        ChatPart part = new ChatPart(base, isCmd, isHover, isSetInChat);

        if (isCmd) part.setCmd(cmd);
        if (isHover) part.setHoverString(otherFont ? SmallFontConverter.toSmallFont(hover) : hover);
        if (isSetInChat) part.setSetInChatString(setInChatString);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addLinkMessage(String msg, String link, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        ChatPart part = new ChatPart(base, false, false, false);

        part.setOpenLink(link);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(part);
        return this;
    }

    public MessageBuilder addMessage(String msg, boolean otherFont) {
        Component base = Component.text(otherFont
                ? SmallFontConverter.toSmallFont(msg.toLowerCase())
                : msg);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(new ChatPart(base, false, false, false));
        return this;
    }

    public MessageBuilder addMessage(String hex, String msg, boolean otherFont, boolean bold) {
        Component base = MessageBuilder.createHexColorMessage(
                hex,
                msg,
                otherFont,
                bold
        ).decoration(TextDecoration.ITALIC, false);

        lines.computeIfAbsent(currentLine, k -> new ArrayList<>()).add(new ChatPart(base, false, false, false));
        return this;
    }

    public MessageBuilder addMessage(String fromHex, String toHex, String msg, boolean otherFont, boolean bold) {
        Component base = MessageBuilder.createGradientMessage(
                otherFont ? SmallFontConverter.toLowerCaseSmallFont(msg) : msg,
                Color.decode(fromHex),
                Color.decode(toHex)
        ).decoration(TextDecoration.ITALIC, false);

        if(bold) base = base.style(Style.style(TextDecoration.BOLD));

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
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }

            player.sendMessage(base);
        }
        return this;
    }

    public List<String> getAsLore() {
        List<String> result = new ArrayList<>();

        for (var entry : lines.entrySet()) {
            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }

            result.add(LegacyComponentSerializer.legacySection().serialize(base));
        }
        return result;
    }

    public String getAsString() {
        StringBuilder result = new StringBuilder();

        for (var entry : lines.entrySet()) {
            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

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
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }
            result = result.append(base);
            if (current < lines.size()) result = result.appendNewline();
        }
        return result;
    }

    public MessageBuilder send(Collection<? extends Player> players) {
        players.forEach(this::send);
        return this;
    }

    public MessageBuilder sendActionBar(Player player) {
        for (var entry : lines.entrySet()) {

            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }

            player.sendActionBar(base);
        }
        return this;
    }

    public MessageBuilder sendActionBar(Collection<? extends Player> players) {
        players.forEach(this::sendActionBar);
        return this;
    }

    public MessageBuilder sendActionBar(CommandSender sender) {
        for (var entry : lines.entrySet()) {

            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }

            sender.sendActionBar(base);
        }
        return this;
    }

    public MessageBuilder send(CommandSender sender) {
        for (var entry : lines.entrySet()) {

            Component base = Component.empty();

            for (ChatPart part : entry.getValue()) {
                Component text = part.getText();

                if (part.isCmd())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.runCommand(part.getCmdString()));

                if (part.isHover())
                    text = text.hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(
                            LegacyComponentSerializer.legacySection().deserialize(part.getHoverString())
                    ));

                if (part.isSetInChat())
                    text = text.clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand(part.getSetInChatString()));

                if(part.getOpenLink() != null) {
                    text = text.clickEvent(ClickEvent.openUrl(part.getOpenLink()));
                }

                base = base.append(text);
            }

            sender.sendMessage(base);
        }
        return this;
    }

    public static Component createHexColorMessage(String hex, String message, boolean otherFont, boolean bold) {
        Color color = Color.decode(hex);
        if(!bold) return Component.text((otherFont ? SmallFontConverter.toLowerCaseSmallFont(message) : message), TextColor.color(color.getRed(), color.getGreen(), color.getBlue()));
        return Component.text((otherFont ? SmallFontConverter.toLowerCaseSmallFont(message) : message), TextColor.color(color.getRed(), color.getGreen(), color.getBlue()), TextDecoration.BOLD);
    }
}