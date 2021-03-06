package eu.rex2go.chat2go.command;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.PermissionConstants;
import eu.rex2go.chat2go.command.exception.CommandNoPermissionException;
import eu.rex2go.chat2go.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends WrappedCommandExecutor {

    public BroadcastCommand(Chat2Go plugin) {
        super(plugin, "broadcast");
    }

    @Override
    protected boolean execute(CommandSender sender, User user, String label, String... args) throws
            CommandNoPermissionException {

        checkPermission(sender, PermissionConstants.PERMISSION_COMMAND_BROADCAST);

        if (args.length < 1) {
            return false;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        message = new StringBuilder(message.substring(0, message.length() - 1));

        String formatted = plugin.getChatManager().formatBroadcast(message.toString());
        Bukkit.broadcastMessage(formatted);

        return true;
    }
}
