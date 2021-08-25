package eu.rex2go.chat2go.command.ignorelist;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.util.MathUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IgnoreListCommand extends WrappedCommandExecutor {

    public IgnoreListCommand() {
        super("ignorelist");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_IGNORE.getPermission());

        getTranslator().sendMessage(sender, "§7-§b-§7- §f{ignore_list} §7-§b-§7-");

        List<String> ignoredList = new ArrayList<>();

        for (UUID ignored : user.getIgnored()) {
            User ignoredUser = Chat2Go.getUserManager().loadUser(ignored, null, false);
            ignoredList.add(ignoredUser.getName());
        }

        int entriesPerPage = 8;
        int entryCount = ignoredList.size();
        int pages = entryCount / entriesPerPage + (entryCount % entriesPerPage == 0 ? 0 : 1);
        int page = 1;

        if (entryCount == 0) {
            user.sendMessage("command.ignorelist.no_ignored", false);
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

        for (int i = offset; i < offset + entriesPerPage; i++) {
            if (entryCount <= i) break;

            String ignore = ignoredList.get(i);
            String deleteTranslation = getTranslator().getTranslation("unignore");
            String deleteHoverTranslation = getTranslator().getTranslation("command.ignorelist.unignore_hover", ignore);

            BaseComponent[] deleteComponents = new ComponentBuilder("§c[" + deleteTranslation + "]")
                    .event(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new Text(deleteHoverTranslation)))
                    .event(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/unignore " + ignore))
                    .create();

            BaseComponent[] components = new ComponentBuilder("§7- §f" + ignore + " ")
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
                                        "/ignorelist " + (page - 1)))
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
                                        "/ignorelist " + (page + 1)))
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

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
