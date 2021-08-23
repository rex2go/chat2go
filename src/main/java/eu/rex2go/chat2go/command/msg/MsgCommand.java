package eu.rex2go.chat2go.command.msg;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CustomErrorCommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotFoundCommandException;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgCommand extends WrappedCommandExecutor {

    public MsgCommand() {
        super("msg");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws Exception {

        checkPermission(sender, ChatPermission.COMMAND_MSG.getPermission());
        checkPlayer(sender);

        if(args.length == 0 && user.isInPrivateChat()) {
            user.setInPrivateChat(false);
            return;
        }

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/msg <{player}> [{message}]");
            return;
        }

        String targetName = args[0];
        if (targetName.equalsIgnoreCase(user.getName())) {
            throw new CustomErrorCommandException("command.message.message_yourself");
        }

        // Message receiver
        Player targetPlayer =
                Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(targetName)).findFirst().orElse(null);

        if (targetPlayer == null) {
            throw new PlayerNotFoundCommandException(targetName);
        }

        // Message receiver user
        User target = Chat2Go.getUserManager().getUser(targetPlayer);

        if(args.length == 1) {
            user.setLastChatter(target);
            user.setInPrivateChat(!user.isInPrivateChat());
            return;
        }

        // Build message, skip username (first argument)
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        // Remove trailing white space
        message = new StringBuilder(message.substring(0, message.length() - 1));

        sendPrivateMessage(user, target, message.toString());
    }

    public static void sendPrivateMessage(User sender, User receiver, String message) {
        receiver.setLastChatter(sender);

        String format = ChatConfig.getFormatPrivateMessage();

        format = Chat2Go.parseColor(format);

        Placeholder senderPlaceholder = new Placeholder("sender", TextComponent.fromLegacyText(sender.getPlayer().getDisplayName()));
        Placeholder senderPrefixPlaceholder = new Placeholder("senderPrefix", TextComponent.fromLegacyText(sender.getPrefix()));
        Placeholder senderSuffixPlaceholder = new Placeholder("senderSuffix", TextComponent.fromLegacyText(sender.getSuffix()));
        Placeholder senderWorldPlaceholder = new Placeholder("senderWorld", TextComponent.fromLegacyText(sender.getPlayer().getWorld().getName()));
        Placeholder senderGroupPlaceholder = new Placeholder("senderGroup", TextComponent.fromLegacyText(sender.getPrimaryGroup()));

        Placeholder receiverPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(receiver.getPlayer().getDisplayName()));
        Placeholder receiverPrefixPlaceholder = new Placeholder("receiverPrefix", TextComponent.fromLegacyText(receiver.getPrefix()));
        Placeholder receiverSuffixPlaceholder = new Placeholder("receiverSuffix", TextComponent.fromLegacyText(receiver.getSuffix()));
        Placeholder receiverWorldPlaceholder = new Placeholder("receiverWorld", TextComponent.fromLegacyText(receiver.getPlayer().getWorld().getName()));
        Placeholder receiverGroupPlaceholder = new Placeholder("receiverGroup", TextComponent.fromLegacyText(receiver.getPrimaryGroup()));

        Placeholder messagePlaceholder = new Placeholder("message", TextComponent.fromLegacyText(message));

        BaseComponent[] components = PlaceholderProcessor.process(
                format,
                sender.getPlayer(),
                senderPlaceholder,
                senderPrefixPlaceholder,
                senderSuffixPlaceholder,
                senderWorldPlaceholder,
                senderGroupPlaceholder,
                receiverPlaceholder,
                receiverPrefixPlaceholder,
                receiverSuffixPlaceholder,
                receiverWorldPlaceholder,
                receiverGroupPlaceholder,
                messagePlaceholder);

        sender.getPlayer().spigot().sendMessage(components);
        receiver.getPlayer().spigot().sendMessage(components);
    }
}
