package eu.rex2go.chat2go.command.broadcast;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.NoPermissionCommandException;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends WrappedCommandExecutor {

    public BroadcastCommand() {
        super("broadcast");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws
            NoPermissionCommandException {

        checkPermission(sender, ChatPermission.COMMAND_BROADCAST.getPermission());

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/broadcast <{message}>");
            return;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        message = new StringBuilder(message.substring(0, message.length() - 1));

        String formatted = ChatConfig.getFormatBroadcast();
        formatted = formatted.replace("{message}", message);
        formatted = Chat2Go.parseColor(formatted);

        Bukkit.broadcastMessage(formatted);
    }
}
