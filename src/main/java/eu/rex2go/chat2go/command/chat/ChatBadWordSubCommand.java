package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.SubCommand;
import eu.rex2go.chat2go.filter.BadWordFilter;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.util.MathUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ChatBadWordSubCommand extends SubCommand {

    public ChatBadWordSubCommand(ChatCommand command) {
        super(command);
    }

    @Override
    public void execute(CommandSender sender, User user, String label, String[] args) throws Exception {
        checkPermission(sender, ChatPermission.COMMAND_CHAT_BADWORD.getPermission());

        if (args.length < 2) {
            getTranslator().sendMessage(sender, "§7/chat badword <list|add|remove>");
            return;
        }

        String subCommand = args[1];

        if (subCommand.equalsIgnoreCase("list")) {
            getTranslator().sendMessage(sender, "§7-§b-§7- §f{bad_words} §7-§b-§7-");

            List<String> badWords = BadWordFilter.getBadWords();

            int entriesPerPage = 8;
            int entryCount = badWords.size();
            int pages = entryCount / entriesPerPage + (entryCount % entriesPerPage == 0 ? 0 : 1);
            int page = 1;

            if (entryCount == 0) {
                user.sendMessage("command.chat.badword.list.no_bad_words", false);
                return;
            }

            if (args.length > 2) {
                String pageStr = args[2];

                if (MathUtil.isNumber(pageStr)) {
                    page = Integer.parseInt(pageStr);

                    if (page > pages) page = pages;
                    if (page < 1) page = 1;
                }
            }

            int offset = entriesPerPage * (page - 1);

            // list bad words
            for (int i = offset; i < offset + entriesPerPage; i++) {
                if (entryCount <= i) break;

                String badWord = badWords.get(i);
                String deleteTranslation = getTranslator().getTranslation("delete");
                String deleteHoverTranslation = getTranslator().getTranslation("command.chat.badword.list.delete_hover", badWord);

                BaseComponent[] deleteComponents = new ComponentBuilder("§c[" + deleteTranslation + "]")
                        .event(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new Text(deleteHoverTranslation)))
                        .event(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/chat badword remove " + badWord))
                        .create();

                BaseComponent[] components = new ComponentBuilder("§7- §f" + badWord + " ")
                        .append(deleteComponents)
                        .create();

                sender.spigot().sendMessage(components);
            }

            if (pages > 1) {
                ComponentBuilder componentBuilder = new ComponentBuilder();

                if (page != 1) {
                    componentBuilder.append(
                            new ComponentBuilder(" §f[<]")
                                    .event(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            new Text(getTranslator().getTranslation("previous_page"))))
                                    .event(new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/chat badword list " + (page - 1)))
                                    .create()
                    );
                }

                if (pages != page) {
                    componentBuilder.append(
                            new ComponentBuilder(" §f[>]")
                                    .event(new HoverEvent(
                                            HoverEvent.Action.SHOW_TEXT,
                                            new Text(getTranslator().getTranslation("next_page"))))
                                    .event(new ClickEvent(
                                            ClickEvent.Action.RUN_COMMAND,
                                            "/chat badword list " + (page + 1)))
                                    .create()
                    );
                }

                String translation = getTranslator().getTranslation(
                        "pagination",
                        String.valueOf(page),
                        String.valueOf(pages));


                BaseComponent[] components = new ComponentBuilder(translation).append(componentBuilder.create()).create();

                sender.spigot().sendMessage(components);
            }
        } else if (subCommand.equalsIgnoreCase("add")) {
            if (args.length < 3) {
                getTranslator().sendMessage(sender, "§7/chat badword add <word>");
                return;
            }

            String badWord = args[2];

            if (BadWordFilter.addBadWord(badWord)) {
                user.sendMessage("command.chat.badword.add.success", false, badWord);
            } else {
                user.sendMessage("command.chat.badword.add.error", false, badWord);
            }
        } else if (subCommand.equalsIgnoreCase("remove")) {
            if (args.length < 3) {
                getTranslator().sendMessage(sender, "§7/chat badword remove <word>");
                return;
            }

            String badWord = args[2];

            if (BadWordFilter.removeBadWord(badWord)) {
                user.sendMessage("command.chat.badword.remove.success", false, badWord);
            } else {
                user.sendMessage("command.chat.badword.remove.error", false, badWord);
            }
        } else {
            getTranslator().sendMessage(sender, "§7/chat badword <list|add|remove>");
        }
    }
}
