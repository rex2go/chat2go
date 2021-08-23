package eu.rex2go.chat2go.command.reply;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.command.WrappedCommandExecutor;
import eu.rex2go.chat2go.command.exception.CustomErrorCommandException;
import eu.rex2go.chat2go.command.exception.PlayerNotOnlineCommandException;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends WrappedCommandExecutor {

    public ReplyCommand() {
        super("reply");
    }

    @Override
    protected void execute(CommandSender sender, User user, String label, String... args) throws Exception {

        checkPermission(sender, ChatPermission.COMMAND_MSG.getPermission());
        checkPlayer(sender);

        Player player = user.getPlayer();
        Player senderPlayer = (Player) sender;

        if (args.length < 1) {
            getTranslator().sendMessage(sender, "§7/msg <{message}>");
            return;
        }

        // User to reply to
        User target = user.getLastChatter();

        if (target == null) {
            throw new CustomErrorCommandException("command.message.no_player_to_reply_to");
        }

        Player targetPlayer = target.getPlayer();

        if (targetPlayer == null) {
            throw new PlayerNotOnlineCommandException(target.getName());
        }

        // Build message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        // Remove trailing white space
        message = new StringBuilder(message.substring(0, message.length() - 1));

        target.setLastChatter(user);

        String format = ChatConfig.getFormatPrivateMessage();

        format = Chat2Go.parseColor(format);

        Placeholder senderPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(senderPlayer.getPlayer().getDisplayName()));
        Placeholder senderPrefixPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder senderSuffixPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(user.getSuffix()));
        Placeholder senderWorldPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(senderPlayer.getPlayer().getWorld().getName()));
        Placeholder senderGroupPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(user.getPrimaryGroup()));

        Placeholder receiverPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(targetPlayer.getDisplayName()));
        Placeholder receiverPrefixPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(target.getPrefix()));
        Placeholder receiverSuffixPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(target.getSuffix()));
        Placeholder receiverWorldPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(targetPlayer.getPlayer().getWorld().getName()));
        Placeholder receiverGroupPlaceholder = new Placeholder("receiver", TextComponent.fromLegacyText(target.getPrimaryGroup()));

        Placeholder messagePlaceholder = new Placeholder("message", TextComponent.fromLegacyText(message.toString()));

        BaseComponent[] components = PlaceholderProcessor.process(
                format,
                senderPlayer,
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

        targetPlayer.spigot().sendMessage(components);
        player.spigot().sendMessage(components);
    }
}
