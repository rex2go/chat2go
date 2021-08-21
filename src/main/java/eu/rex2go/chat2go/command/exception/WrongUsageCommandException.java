package eu.rex2go.chat2go.command.exception;

import org.bukkit.ChatColor;

public class WrongUsageCommandException extends CommandException {

    public WrongUsageCommandException(String usage) {
        super(ChatColor.GRAY + usage);
    }
}

