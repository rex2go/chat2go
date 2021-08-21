package eu.rex2go.chat2go.command;

import eu.rex2go.chat2go.user.User;
import org.bukkit.command.CommandSender;

public abstract class SubCommand extends BaseCommand {

    protected WrappedCommandExecutor command;

    public SubCommand(WrappedCommandExecutor command) {
        this.command = command;
    }

    public abstract void execute(CommandSender sender, User user, String label, String[] args) throws Exception;
}
