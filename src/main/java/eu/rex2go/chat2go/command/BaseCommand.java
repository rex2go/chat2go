package eu.rex2go.chat2go.command;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.command.exception.NoPermissionCommandException;
import eu.rex2go.chat2go.command.exception.NoPlayerCommandException;
import eu.rex2go.chat2go.translator.Translator;
import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class BaseCommand {


    protected abstract void execute(CommandSender sender, User user, String label, String... args) throws Exception;

    public void checkPermission(CommandSender sender, String... permissions) throws NoPermissionCommandException {
        if (Arrays.stream(permissions).noneMatch(sender::hasPermission))
            throw new NoPermissionCommandException(permissions[0]);
    }

    public void checkPlayer(CommandSender sender) throws NoPlayerCommandException {
        if (!(sender instanceof Player)) throw new NoPlayerCommandException();
    }

    public Translator getTranslator() {
        return Chat2Go.getTranslator();
    }
}
