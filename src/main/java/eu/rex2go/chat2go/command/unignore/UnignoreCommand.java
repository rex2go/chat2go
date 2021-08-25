package eu.rex2go.chat2go.command.unignore;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class UnignoreCommand extends WrappedCommandExecutor {

    public UnignoreCommand() {
        super("unignore");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_IGNORE.getPermission());

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/unignore <{player}>");
            return;
        }

        String targetName = args[0];
        User target = Chat2Go.getUserManager().loadUser(null, targetName, false);

        if (target == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        if (!user.getIgnored().contains(target.getUuid())) {
            user.sendMessage("command.unignore.not_ignoring", false, target.getName());
        } else {
            user.unignore(target);
            user.sendMessage("command.unignore.unignored", false, target.getName());
        }

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
