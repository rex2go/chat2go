package eu.rex2go.chat2go.command.unmute;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class UnmuteCommand extends WrappedCommandExecutor {

    public UnmuteCommand() {
        super("unmute");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_MUTE.getPermission());

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/unmute <{player}>");
            return;
        }

        String targetName = args[0];
        User target = Chat2Go.getUserManager().loadUser(null, targetName, false);

        if (target == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        if(target.getMute() == null) {
            user.sendMessage("command.unmute.not_muted", false, target.getName());
            Chat2Go.getUserManager().unloadOffline(user);
            return;
        }

        target.unmute();

        user.sendMessage("command.unmute.unmuted", false, target.getName());
        target.sendMessage("command.unmute.unmuted_target", false);

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
