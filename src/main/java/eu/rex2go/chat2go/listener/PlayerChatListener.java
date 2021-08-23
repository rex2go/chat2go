package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.chat.AntiSpam;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.exception.FilterException;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.Mute;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
        if(eventPriority == EventPriority.LOWEST) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChatLow(AsyncPlayerChatEvent event) {
        if(eventPriority == EventPriority.LOW) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChatNormal(AsyncPlayerChatEvent event) {
        if(eventPriority == EventPriority.NORMAL) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
        if(eventPriority == EventPriority.HIGH) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatHighest(AsyncPlayerChatEvent event) {
        if(eventPriority == EventPriority.HIGHEST) {
            onPlayerChat(event);
        }
    }

    // TODO extra class
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChatMonitor(AsyncPlayerChatEvent event) {
        if(eventPriority == EventPriority.MONITOR) {
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

        // chat disabled
        if (!ChatConfig.isChatEnabled()) {
            event.setCancelled(true);
            user.sendMessage("chat.disabled", false);
            return;
        }

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

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message);
        String group = user.getPrimaryGroup();
        String chatFormat = ChatConfig.getChatFormatFormat();

        // group formats
        if(ChatConfig.getChatFormatGroupFormats().containsKey(group)) {
            chatFormat = ChatConfig.getChatFormatGroupFormats().get(group);
        }

        chatFormat = Chat2Go.parseColor(chatFormat);

        String username = ChatConfig.useCompatibilityMode() ? "%1$s" : user.getPlayer().getDisplayName();

        Placeholder usernamePlaceholder = new Placeholder("username", TextComponent.fromLegacyText(username));
        Placeholder messagePlaceholder = new Placeholder("message", ChatConfig.useCompatibilityMode()
                ? TextComponent.fromLegacyText("%2$s") : messageComponents);
        Placeholder prefixPlaceholder = new Placeholder("prefix", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder suffixPlaceholder = new Placeholder("suffix", TextComponent.fromLegacyText(user.getSuffix()));
        Placeholder worldPlaceholder = new Placeholder("world", TextComponent.fromLegacyText(player.getWorld().getName()));
        Placeholder groupPlaceholder = new Placeholder("group", TextComponent.fromLegacyText(user.getPrimaryGroup()));

        BaseComponent[] format = PlaceholderProcessor.process(
                chatFormat,
                player,
                usernamePlaceholder,
                messagePlaceholder,
                prefixPlaceholder,
                suffixPlaceholder,
                worldPlaceholder,
                groupPlaceholder);

        // fix message color
        for(int i = format.length - 1;  i > 0; i--) {
            BaseComponent baseComponent = format[i];

            if(baseComponent.getColorRaw() != null) {
                for(BaseComponent messageComponent : messageComponents) {
                    if(messageComponent.getColorRaw() != null) break;

                    messageComponent.setColor(baseComponent.getColorRaw());
                }

                break;
            }
        }

        event.setMessage(TextComponent.toLegacyText(messageComponents));
        event.setFormat(TextComponent.toLegacyText(format));

        // check if messages should be sent manually
        if (ChatConfig.useCompatibilityMode()) {
            return;
        }

        // stop bukkit from sending the chat message
        event.setCancelled(true);

        // collect recipients
        Collection<? extends Player> recipients = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (ChatConfig.isChatWorldChatEnabled()) {
            recipients =
                    recipients.stream().filter(
                            r -> r.getWorld().equals(player.getWorld())
                    ).collect(Collectors.toList());

            if (ChatConfig.isWorldChatConsiderRange()) {
                recipients =
                        recipients.stream().filter(
                                r -> r.getLocation().distance(player.getLocation()) <= ChatConfig.getChatWorldChatRange()
                        ).collect(Collectors.toList());
            }
        }

        // send messages individually
        for (Player all : recipients) {
            all.spigot().sendMessage(format);
        }

        // show in log
        plugin.getLogger().log(Level.INFO, TextComponent.toLegacyText(format));
    }
}
