package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.SubCommand;
import eu.rex2go.chat2go.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatClearSubCommand extends SubCommand {

    public ChatClearSubCommand(ChatCommand command) {
        super(command);
    }

    @Override
    public void execute(CommandSender sender, User user, String label, String[] args) throws Exception {
        checkPermission(sender, ChatPermission.COMMAND_CHAT_CLEAR.getPermission());

        for (Player all : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                all.sendMessage("  ");
            }
        }
    }
}
