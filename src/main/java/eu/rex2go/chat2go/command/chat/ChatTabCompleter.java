package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.filter.BadWordFilter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("chat")) {

            String lastArg = args[args.length - 1];

            if (args.length == 1) {
                ArrayList<String> list = new ArrayList<>();

                list.add("help");
                list.add("reload");
                list.add("clear");
                list.add("badword");

                if (lastArg.isEmpty()) return list;

                return list
                        .stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }

            String subCommand = args[0];

            if (subCommand.equalsIgnoreCase("badword")) {
                if (args.length == 2) {
                    ArrayList<String> list = new ArrayList<>();

                    list.add("list");
                    list.add("add");
                    list.add("remove");

                    if (lastArg.isEmpty()) return list;

                    return list
                            .stream()
                            .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }

                if (args.length == 3) {
                    String subSubCommand = args[1];

                    if (subSubCommand.equalsIgnoreCase("remove")) {
                        List<String> list = BadWordFilter.getBadWords();

                        if (lastArg.isEmpty()) return list;

                        return list
                                .stream()
                                .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}
