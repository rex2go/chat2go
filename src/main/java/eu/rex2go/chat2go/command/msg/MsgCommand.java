package eu.rex2go.chat2go.command.msg;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CustomErrorCommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends WrappedCommandExecutor {

    public MsgCommand() {
        super("msg");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws Exception {

        checkPermission(sender, ChatPermission.COMMAND_MSG.getPermission());
        checkPlayer(sender);

        if(args.length == 0 && user.isInPrivateChat()) {
            user.setInPrivateChat(false);
            return;
        }

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/msg <{player}> [{message}]");
            return;
        }

        String targetName = args[0];
        if (targetName.equalsIgnoreCase(user.getName())) {
            throw new CustomErrorCommandException("command.message.message_yourself");
        }

        // Message recipient
        Player targetPlayer =
                Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);

        if (targetPlayer == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        // Message recipient user
        User target = Chat2Go.getUserManager().getUser(targetPlayer);

        if(args.length == 1) {
            user.setLastChatter(target);
            user.setInPrivateChat(!user.isInPrivateChat());
            return;
        }

        // Build message, skip username (first argument)
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        // Remove trailing white space
        message = new StringBuilder(message.substring(0, message.length() - 1));

        target.sendPrivateMessage(user, message.toString());
    }
}
