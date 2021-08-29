package eu.rex2go.chat2go.command.spy;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class SpyCommand extends WrappedCommandExecutor {

    public SpyCommand() {
        super("spy");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_SPY.getPermission());

        if (args.length == 0 && user.getSpyTarget() != null) {
            user.setSpyTarget(null);
            return;
        }

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/spy <{player}> [-c]");
            return;
        }

        boolean spyCommands = false;

        if(args.length > 1) {
            spyCommands = args[1].equalsIgnoreCase("-c");
        }

        String targetName = args[0];
        User target = Chat2Go.getUserManager().loadUser(null, targetName, false);

        if (target == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        if(target.equals(user)) {
            user.sendMessage("command.spy.spy_yourself", false);
            return;
        }

        if(target.equals(user.getSpyTarget())) {
            user.setSpyTarget(null);
        } else {
            user.setSpyTarget(target);
            user.setSpyCommands(spyCommands);
        }

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
