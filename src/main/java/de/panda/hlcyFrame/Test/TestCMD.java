package de.panda.hlcyFrame.Test;

import de.panda.hlcyFrame.Command.HlcyCMD;
import de.panda.hlcyFrame.Command.HlcyCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class TestCMD {

    @HlcyCMD
    public void init() {
        HlcyCommand test = new HlcyCommand("heal")
                .addAlias("hallo")
                .description("inhalt gegeben")
                .usage("joar keine ahnung")
                .allowedSender(HlcyCommand.Allowed_Sender.PLAYER)
                .onExecute(e -> {
                    e.player().sendMessage(e.getText(2));
                    Player player = e.onlinePlayers()[0];
                    if (player == null) {
                        e.player().sendMessage("target not found!");
                        return;
                    }

                    double heal = 20;
                    if (e.args().length == 2 && e.doubles()[1] == null) {
                        e.player().sendMessage("invalid number: " + e.args()[1]);
                        return;
                    }
                    if (e.doubles()[1] != null) heal = e.doubles()[1];
                    player.heal(heal);
                    player.sendMessage("you have been healed by " + e.player().getName() + " by " + heal + "hp");
                    e.player().sendMessage("healed " + player.getName() + " by " + heal + "hp");
                })
                .newArgument("target")
                .newArgument("amount")
                .setInfinite()
                .command()
                .onTabComplete((p, args) -> {
                    LinkedList<String> result = new LinkedList<>();
                    if (args.length == 1) {
                        result.addAll(Bukkit.getOnlinePlayers().stream().filter(player -> player != p).map(Player::getName).sorted().collect(Collectors.toCollection(LinkedHashSet::new)));
                        if(result.isEmpty()) {
                            result.add(" ");
                            result.add("<[target]>");
                        }
                    }

                    if (args.length == 2) {
                        result.add("1");
                        result.add("5");
                        result.add("10");
                        result.add("15");
                        result.add("20");
                    }

                    return result.stream().toList();
                })
                .build();
    }
}
