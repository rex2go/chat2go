package eu.rex2go.chat2go.command.exception;

import eu.rex2go.chat2go.Chat2Go;
import org.bukkit.ChatColor;

public class CommandException extends Exception {

    public CommandException(String message, String... args) {
        super(ChatColor.RED + Chat2Go.getTranslator().getTranslation(message, args));
    }
}
