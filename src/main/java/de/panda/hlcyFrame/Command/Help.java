package de.panda.hlcyFrame.Command;

import de.panda.hlcyFrame.HlcyFrame;

import java.util.LinkedList;

public class Help {

    @HlcyCMD
    public void init() {
        HlcyCommand helpCMD = new HlcyCommand("hilfe")
                .addAlias("help")
                .usage("/hilfe")
                .description("helps for all commands")
                .newArgument("<[commands]>")
                .command()
                .onTabComplete((p, args) -> {
                    LinkedList<String> result = new LinkedList<>();
                    if(args.length == 1) {
                        for(HlcyCommand commands : HlcyFrame.getCommands()) {
                            result.add(commands.getName());
                        }
                    }
                    return result;
                })
                .onExecute(e -> {
                    e.player().sendMessage(e.args());
                })
                .build();
    }
}
