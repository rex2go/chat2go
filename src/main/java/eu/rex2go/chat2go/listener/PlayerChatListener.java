package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.chat.AntiSpam;
import eu.rex2go.chat2go.command.msg.MsgCommand;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.exception.FilterException;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.Mute;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class PlayerChatListener extends AbstractListener {

    private EventPriority eventPriority;

    public PlayerChatListener() {
        String eventPriorityString = ChatConfig.getGeneralEventPriority();

        try {
            eventPriority = EventPriority.valueOf(eventPriorityString.toUpperCase());
        } catch (Exception exception) {
            eventPriority = EventPriority.HIGHEST;
            Chat2Go.getInstance().getLogger().log(Level.WARNING, "Unrecognized event priority: " + eventPriorityString);
            Chat2Go.getInstance().getLogger().log(Level.WARNING, "Falling back to HIGHEST");
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatLowest(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.LOWEST) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChatLow(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.LOW) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChatNormal(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.NORMAL) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.HIGH) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatHighest(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.HIGHEST) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
        if (eventPriority == EventPriority.MONITOR) {
            onPlayerChat(event);
        }
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        User user = Chat2Go.getUser(player);

        if (user == null) {
            event.setCancelled(true);
            player.sendMessage("§cError");
            return;
        }

        // private chat
        if (user.isInPrivateChat()) {
            MsgCommand.sendPrivateMessage(user, user.getLastChatter(), event.getMessage());
            event.setCancelled(true);
            return;
        }

        // chat disabled
        if (!ChatConfig.isChatEnabled()) {
            event.setCancelled(true);
            user.sendMessage("chat.disabled", false);
            return;
        }

        // player muted
        if (user.isMuted()
                && !user.hasPermission(ChatPermission.BYPASS_MUTE.getPermission())) {
            Mute mute = user.getMute();

            if (mute.getReason() != null) {
                user.sendMessage("chat.you_have_been_muted_reason", false, mute.getRemainingTimeString(), mute.getReason());
            } else {
                user.sendMessage("chat.you_have_been_muted", false, mute.getRemainingTimeString());
            }

            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();

        // anti spam
        if (ChatConfig.isAntiSpamEnabled()
                && !user.hasPermission(ChatPermission.BYPASS_ANTISPAM.getPermission())) {
            AntiSpam.CheckResult checkResult = AntiSpam.check(message, user);

            if (checkResult.isBlockMessage()) {
                user.sendMessage(checkResult.getMessage(), false);
                event.setCancelled(true);
                return;
            }

            message = AntiSpam.preventCaps(message);
        }

        // filter message
        if (ChatConfig.isFilterEnabled()
                && !user.hasPermission(ChatPermission.BYPASS_FILTER.getPermission())) {
            try {
                message = Chat2Go.getChatManager().filter(message);
            } catch (FilterException exception) { // filter matched, block message mode
                player.sendMessage(exception.getMessage());
                event.setCancelled(true);

                // staff notification
                if (ChatConfig.isNotificationFilterEnabled()) {
                    for (User staff : Chat2Go.getUserManager().getUsers()) {
                        if (staff.getPlayer().hasPermission(ChatPermission.NOTIFY_FILTER.getPermission())) {
                            staff.getPlayer().sendMessage(
                                    Chat2Go.PREFIX + " " + Chat2Go.WARNING_PREFIX + " " + player.getName() + ": " + ChatColor.RED + message
                            );
                        }
                    }
                }

                return;
            }
        }

        user.setLastMessage(message);

        // parse colors
        if (user.hasPermission(ChatPermission.CHAT_COLOR.getPermission())
                && ChatConfig.isChatFormatTranslateChatColors()) {
            message = Chat2Go.parseColor(message);
        }

        // escape % because without compatibility mode message is hard coded in format
        if (!ChatConfig.useCompatibilityMode()) {
            message = message.replace("%", "%%");
        }

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message);
        String group = user.getPrimaryGroup();
        String chatFormat = ChatConfig.getChatFormatFormat();

        // group formats
        if (ChatConfig.getChatFormatGroupFormats().containsKey(group)) {
            chatFormat = ChatConfig.getChatFormatGroupFormats().get(group);
        }

        // parse format color
        chatFormat = Chat2Go.parseColor(chatFormat);

        BaseComponent[] format = getFormat(user, null, chatFormat, messageComponents);

        // fix message color
        for (int i = format.length - 1; i > 0; i--) {
            BaseComponent baseComponent = format[i];

            if (baseComponent.getColorRaw() != null) {
                for (BaseComponent messageComponent : messageComponents) {
                    if (messageComponent.getColorRaw() != null) break;

                    messageComponent.setColor(baseComponent.getColorRaw());
                }

                break;
            }
        }

        try {
            event.setMessage(TextComponent.toLegacyText(messageComponents));
            event.setFormat(TextComponent.toLegacyText(format));
        } catch (Exception exception) {
            Chat2Go.getInstance().getLogger().log(Level.SEVERE, "Spigot formatting error: " + exception.getMessage());
            Chat2Go.getInstance().getLogger().log(Level.SEVERE, "Your chat format is invalid.");
        }

        // avoid concurrent modification exception
        Set<Player> recipients = new HashSet<>(event.getRecipients());

        for(Player recipient : recipients) {
            User recipientUser = Chat2Go.getUser(recipient);

            if(recipientUser.getIgnored().contains(user.getUuid())) {
                event.getRecipients().remove(recipient);
            }
        }

        // update
        recipients = new HashSet<>(event.getRecipients());

        if (ChatConfig.isChatWorldChatEnabled()) {
            for (Player recipient : recipients) {
                if (!recipient.getWorld().equals(player.getWorld())) {
                    event.getRecipients().remove(recipient);
                    continue;
                }

                if (ChatConfig.isWorldChatConsiderRange()
                        && recipient.getLocation().distance(player.getLocation()) > ChatConfig.getChatWorldChatRange()) {
                    event.getRecipients().remove(recipient);
                }
            }
        }

        // check if messages should be sent manually
        if (ChatConfig.useCompatibilityMode()) {
            return;
        }

        // stop bukkit from sending the chat message, fix for e.g. DiscordSRV
        event.getRecipients().clear();

        // workaround double percent bug
        message = TextComponent.toLegacyText(messageComponents);
        message = String.format(message);
        messageComponents = TextComponent.fromLegacyText(message);

        // update format
        format = getFormat(user, null, chatFormat, messageComponents);

        // send messages individually
        for (Player recipient : recipients) {
            if (ChatConfig.isGeneralRelationalPlaceholders()) {
                format = getFormat(user, recipient, chatFormat, messageComponents);
                recipient.spigot().sendMessage(format);
                continue;
            }

            recipient.spigot().sendMessage(format);
        }
    }

    private BaseComponent[] getFormat(User user, @Nullable Player recipient, String chatFormat, BaseComponent[] messageComponents) {
        String username = ChatConfig.useCompatibilityMode() ? "%1$s" : user.getPlayer().getDisplayName();

        Placeholder usernamePlaceholder = new Placeholder("username", TextComponent.fromLegacyText(username));
        Placeholder messagePlaceholder = new Placeholder("message", ChatConfig.useCompatibilityMode()
                ? TextComponent.fromLegacyText("%2$s") : messageComponents);
        Placeholder prefixPlaceholder = new Placeholder("prefix", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder suffixPlaceholder = new Placeholder("suffix", TextComponent.fromLegacyText(user.getSuffix()));
        Placeholder worldPlaceholder = new Placeholder("world", TextComponent.fromLegacyText(user.getPlayer().getWorld().getName()));
        Placeholder groupPlaceholder = new Placeholder("group", TextComponent.fromLegacyText(user.getPrimaryGroup()));

        return PlaceholderProcessor.process(
                chatFormat,
                user.getPlayer(),
                recipient,
                true,
                usernamePlaceholder,
                messagePlaceholder,
                prefixPlaceholder,
                suffixPlaceholder,
                worldPlaceholder,
                groupPlaceholder);
    }
}
