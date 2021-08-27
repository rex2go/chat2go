package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.SubCommand;
import eu.rex2go.chat2go.config.RexConfig;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class ChatReloadSubCommand extends SubCommand {

    public ChatReloadSubCommand(ChatCommand command) {
        super(command);
    }

    @Override
    public void execute(CommandSender sender, User user, String label, String[] args) throws Exception {
        checkPermission(sender, ChatPermission.COMMAND_CHAT_RELOAD.getPermission());

        RexConfig config = Chat2Go.getChatConfig();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloading",
                true,
                config.getFileName()
        );

        config.reload();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloaded",
                true,
                config.getFileName()
        );

        config = Chat2Go.getMessageConfig();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloading",
                true,
                config.getFileName()
        );

        config.reload();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloaded",
                true,
                config.getFileName()
        );

        config = Chat2Go.getFilterConfig();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloading",
                true,
                config.getFileName()
        );

        config.reload();

        Chat2Go.sendMessage(
                sender,
                "command.chat.reload.reloaded",
                true,
                config.getFileName()
        );
    }
}
