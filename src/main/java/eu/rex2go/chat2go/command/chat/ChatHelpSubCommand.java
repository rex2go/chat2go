package eu.rex2go.chat2go.command.chat;

import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.SubCommand;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatHelpSubCommand extends SubCommand {

    public ChatHelpSubCommand(ChatCommand command) {
        super(command);
    }

    @Override
    public void execute(CommandSender sender, User user, String label, String[] args) throws Exception {
        checkPermission(sender, ChatPermission.COMMAND_CHAT.getPermission());

        ChatColor color = ChatColor.of("#4287f5");

        sender.sendMessage(ChatColor.GRAY + "--- " + ChatColor.AQUA + "chat2go help" + ChatColor.GRAY + " ---");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/chat badword " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " manage the bad word list");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/chat reload " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " reload all files");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/chat clear " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " clear the chat");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/msg <player> <message> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " send a private message");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/r <message> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " reply to a private message");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/mute <player> <duration> [reason] " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " mute a player");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/unmute <player> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " unmute a player");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/broadcast <message> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " broadcast a message");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/force <player> <command|message> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " force a player");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/ignore <player> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " ignore a player");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/unignore <player> " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " unignore a player");

        sender.sendMessage(ChatColor.WHITE + "- " + color + "/ignorelist " + ChatColor.WHITE + " | "
                + ChatColor.GRAY + " list ignored players");

        sender.sendMessage(ChatColor.GRAY + "---                  ---");
    }
}
