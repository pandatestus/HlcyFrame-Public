package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.HlcyFrame;
import de.panda.hlcyFrame.Message.MessageBuilder;

import java.util.LinkedList;

public class Help {

    @HlcyCMD
    public void init() {
        HlcyCommand helpCMD = new HlcyCommand("helpme")
                .addAlias("hilfe")
                .usage("/helpme")
                .description("help for all commands")
                .newArgument("<[commands]>")
                .command()
                .onTabComplete((p, args) -> {
                    LinkedList<String> result = new LinkedList<>();
                    if (args.length == 1) {
                        for (HlcyCommand commands : HlcyFrame.getCommands()) {
                            if (commands.getName().equalsIgnoreCase("helpme")) continue;
                            result.add(commands.getName());
                        }
                    }
                    return result;
                })
                .onExecute(e -> {
                    if(e.args().length == 0) {
                        new MessageBuilder()
                                .addMessage("§b------------- General Help -------------", HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage("§7Usage: /helpme <[command]>", HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage("§7The helpme command will help you with almost any command, just follow the usage instructions", HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage("§b---------------------------------------", HlcyFrame.isOtherFont())
                                .send(e.sender());
                    }

                    if (e.args().length == 1) {
                        HlcyCommand command = null;
                        for (HlcyCommand commands : HlcyFrame.getCommands()) {
                            if (commands.getName().equalsIgnoreCase(e.args()[0])) command = commands;
                        }

                        if (command == null) {
                            new MessageBuilder()
                                    .setHlcy()
                                    .addMessage("§cUnknown command name!", HlcyFrame.isOtherFont())
                                    .send(e.sender());
                            return;
                        }

                        new MessageBuilder()
                                .addMessage("§b------------- Help for §6" + command.getName() + " §b-------------", HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage("§7Usage: " + command.getUsage(), HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage(command.getDescription(), HlcyFrame.isOtherFont())
                                .newLine()
                                .addMessage("§b------------------" + "-".repeat(command.getName().length()) +"------------------", HlcyFrame.isOtherFont())
                                .send(e.sender());
                    }
                })
                .build();
    }
}
