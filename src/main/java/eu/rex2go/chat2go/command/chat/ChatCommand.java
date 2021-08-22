package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public class ChatCommand extends WrappedCommandExecutor {

    private final ChatReloadSubCommand chatReloadSubCommand;
    private final ChatClearSubCommand chatClearSubCommand;
    private final ChatBadWordSubCommand chatBadWordSubCommand;

    public ChatCommand() {
        super("chat", new ChatTabCompleter());

        chatReloadSubCommand = new ChatReloadSubCommand(this);
        chatClearSubCommand = new ChatClearSubCommand(this);
        chatBadWordSubCommand = new ChatBadWordSubCommand(this);
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws Exception {
        checkPermission(sender, ChatPermission.COMMAND_CHAT.getPermission());

        if (args.length == 0) {
            sender.sendMessage("§r");
            sender.sendMessage("§bchat2go §fv" + Chat2Go.getInstance().getDescription().getVersion());
            sender.sendMessage("§7created with §c♥ §7by rex2go");
            sender.sendMessage("§r");
            Chat2Go.sendMessage(sender, "command.chat.type_help", false);
            return;
        }

        String subCommand = args[0];

        if (subCommand.equalsIgnoreCase("reload")) {
            chatReloadSubCommand.execute(sender, user, label, args);
        } else if (subCommand.equalsIgnoreCase("clear")) {
            chatClearSubCommand.execute(sender, user, label, args);
        } else if (subCommand.equalsIgnoreCase("badword")) {
            chatBadWordSubCommand.execute(sender, user, label, args);
        }
    }
}
