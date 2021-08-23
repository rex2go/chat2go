package eu.rex2go.chat2go.command.force;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.CustomErrorCommandException;
import eu.rex2go.chat2go.command.exception.NoPermissionCommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceCommand extends WrappedCommandExecutor {

    public ForceCommand() {
        super("force");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws
            CommandException {

        checkPermission(sender, ChatPermission.COMMAND_FORCE.getPermission());

        if (args.length < 2) {
            getTranslator().sendMessage(sender, "§7/force <{player}> <{command}>");
            return;
        }

        String targetName = args[0];
        if (targetName.equalsIgnoreCase(user.getName())) {
            throw new CustomErrorCommandException("command.force.force_yourself");
        }

        // Message recipient
        Player targetPlayer =
                Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);

        if (targetPlayer == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        User target = Chat2Go.getUserManager().getUser(targetPlayer);

        // Build message, skip username (first argument)
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        // Remove trailing white space
        message = new StringBuilder(message.substring(0, message.length() - 1));

        String messageStr = message.toString();

        if(message.charAt(0) == '/') {
            // command
            Bukkit.dispatchCommand(targetPlayer, message.deleteCharAt(0).toString());
        } else {
            // message
            target.say(messageStr);
        }

        user.sendMessage("command.force.forced", false, target.getDisplayName(), messageStr);
    }
}
