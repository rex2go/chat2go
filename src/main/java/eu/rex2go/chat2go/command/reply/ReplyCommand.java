package eu.rex2go.chat2go.command.reply;

import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CustomErrorCommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotOnlineCommandException;
import eu.rex2go.chat2go.command.msg.MsgCommand;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends WrappedCommandExecutor {

    public ReplyCommand() {
        super("reply");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws Exception {

        checkPermission(sender, ChatPermission.COMMAND_MSG.getPermission());
        checkPlayer(sender);

        if(args.length == 0) {
            if(user.getLastChatter() != null) {
                user.setInPrivateChat(!user.isInPrivateChat());
                return;
            }
        }

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/r <{message}>");
            return;
        }

        // User to reply to
        User target = user.getLastChatter();

        if (target == null) {
            throw new CustomErrorCommandException("command.message.no_player_to_reply_to");
        }

        Player targetPlayer = target.getPlayer();

        if (targetPlayer == null) {
            throw new PlayerNotOnlineCommandException(target.getName());
        }

        // Build message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Remove trailing white space
        message = new StringBuilder(message.substring(0, message.length() - 1));

        MsgCommand.sendPrivateMessage(user, target, message.toString());
    }
}
