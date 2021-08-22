package eu.rex2go.chat2go.command;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.user.User;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public abstract class WrappedCommandExecutor extends BaseCommand implements CommandExecutor {

    private final String command;
    @Getter
    protected PluginCommand pluginCommand;

    public WrappedCommandExecutor(String command, TabCompleter tabCompleter) {
        this.command = command;
        this.pluginCommand = Bukkit.getPluginCommand(command);

        pluginCommand.setExecutor(this);

        if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);

        if (pluginCommand.getPermissionMessage() == null) {
            pluginCommand.setPermissionMessage(Chat2Go.getTranslator().getTranslation("command.no_permission"));
        }
    }

    public WrappedCommandExecutor(String command) {
        this(command, null);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(this.command)) {
            User user = null;

            if (sender instanceof Player) {
                user = Chat2Go.getUserManager().getUser((Player) sender);
            }

            try {
                execute(sender, user, label, args);
            } catch (Exception exception) {
                // TODO
                sender.sendMessage("§c" + exception.getLocalizedMessage());
            }

            return true;
        }

        return false;
    }


}
