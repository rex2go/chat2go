package eu.rex2go.chat2go.command.mute;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.user.Mute;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class MuteCommand extends WrappedCommandExecutor {

    public MuteCommand() {
        super("mute");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws CommandException {

        checkPermission(sender, ChatPermission.COMMAND_MUTE.getPermission());

        if (args.length < 2) {
            getTranslator().sendMessage(sender, "§7/mute <{player}> <{time}> [{reason}]");
            return;
        }

        String targetName = args[0];
        User target = Chat2Go.getUserManager().loadUser(null, targetName, false);

        if(target == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        int seconds = MathUtil.getSeconds(args[1]);

        long ms = seconds * 1000L;
        String message = null;
        long time = System.currentTimeMillis();
        long unmuteTime = time + ms;

        // Build message, skip username (first argument)
        if(args.length > 2) {
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                messageBuilder.append(args[i]).append(" ");
            }

            // Remove trailing white space
            message = messageBuilder.substring(0, messageBuilder.length() - 1);
        }

        Mute mute = new Mute(time, unmuteTime, message, user.getUuid());

        target.setMute(mute);

        if(mute.getReason() != null) {
            user.sendMessage("command.mute.muted_reason", false, target.getName(), mute.getRemainingTimeString(), mute.getReason());
        } else {
            user.sendMessage("command.mute.muted", false, target.getName(), mute.getRemainingTimeString());
        }

        Chat2Go.getUserManager().unloadOffline(user);
    }
}
