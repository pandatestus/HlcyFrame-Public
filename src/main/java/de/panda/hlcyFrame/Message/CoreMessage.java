package de.panda.hlcyFrame.Message;

import net.kyori.adventure.text.Component;

import java.util.Locale;

public class CoreMessage {

    public static Component getMessage(String identifier, boolean otherFont) {

        MessageBuilder mb = new MessageBuilder();
        switch (identifier) {
            case "WRONG_COMMAND_USAGE" -> mb.setHlcy().addMessage("§cIncorrect command usage!", otherFont);
            case "ERROR" -> mb.setHlcy().addMessage("§cError while executing", otherFont);
            case "UNKNOWN_COMMAND" -> mb.setHlcy().addMessage("§cUnknown command!", otherFont);
            case "MISSING_PERMISSION" -> mb.setHlcy().addMessage("§cYou're not allowed to perform that command!", otherFont);
            case "PLAYER_ONLY" -> mb.setHlcy().addMessage("§cThis command is only executable by players!", otherFont);
            case "CONSOLE_ONLY" -> mb.setHlcy().addMessage("§cThis command is only executable via console!", otherFont);
            default -> mb.setHlcy().addMessage("§cNo message was set for this identifier", otherFont);
        }
        return mb.getAsComponent();
    }
}
