package eu.rex2go.chat2go.command.ignore;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class IgnoreCommand extends WrappedCommandExecutor {

    public IgnoreCommand() {
        super("ignore");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_IGNORE.getPermission());

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/ignore <{player}>");
            return;
        }

        String targetName = args[0];

        if(targetName.equalsIgnoreCase(sender.getName())) {
            user.sendMessage("command.ignore.ignore_yourself", false);
            return;
        }

        User target = Chat2Go.getUserManager().loadUser(null, targetName, false);

        if (target == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        if (user.getIgnored().contains(target.getUuid())) {
            user.sendMessage("command.ignore.already_ignoring", false, target.getName());
        } else {
            user.ignore(target);
            user.sendMessage("command.ignore.ignored", false, target.getName());
        }

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
