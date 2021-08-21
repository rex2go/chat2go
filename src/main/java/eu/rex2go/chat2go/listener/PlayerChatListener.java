package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.chat.AntiSpam;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.exception.FilterException;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerChatListener extends AbstractListener {

    @EventHandler(priority = EventPriority.HIGHEST)
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
            /*&& !user.hasPermission(ChatPermission.BYPASS_MUTE.getPermission())*/) { // FIXME disabled for debug purposes
            // TODO send message
            event.setCancelled(true);
            return;
        }

        String message = event.getMessage();

        // anti spam
        if (ChatConfig.isAntiSpamEnabled()
            /*&& !user.hasPermission(ChatPermission.BYPASS_ANTISPAM.getPermission())*/) { // FIXME disabled for debug purposes
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
            /*&& !user.hasPermission(ChatPermission.BYPASS_FILTER.getPermission())*/) { // FIXME disabled for debug purposes
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
        if (user.hasPermission(ChatPermission.CHAT_COLOR.getPermission())) {
            message = Chat2Go.parseColor(message);
        }

        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message);
        String chatFormat = ChatConfig.getChatFormatFormat();

        chatFormat = Chat2Go.parseColor(chatFormat);

        // add chatter name click event
        BaseComponent[] usernameComponents = TextComponent.fromLegacyText(player.getName());
        for (BaseComponent usernameComponent : usernameComponents) {
            usernameComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + player.getName() + " "));
        }

        Placeholder usernamePlaceholder = new Placeholder("username", usernameComponents);
        Placeholder messagePlaceholder = new Placeholder("message", messageComponents);
        Placeholder prefixPlaceholder = new Placeholder("prefix", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder suffixPlaceholder = new Placeholder("suffix", TextComponent.fromLegacyText(user.getSuffix()));

        BaseComponent[] format = PlaceholderProcessor.process(
                chatFormat,
                player,
                usernamePlaceholder,
                messagePlaceholder,
                prefixPlaceholder,
                suffixPlaceholder);

        event.setFormat(TextComponent.toLegacyText(format).replace("%", "%%"));

        // check if messages should be sent manually
        if (ChatConfig.useCompatibilityMode()) {
            return;
        }

        // stop bukkit from sending the chat message
        event.setCancelled(true);

        // collect recipients
        Collection<? extends Player> recipients = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (ChatConfig.isChatWorldChatEnabled()) {
            if (ChatConfig.isWorldChatConsiderRange()) {
                recipients =
                        recipients.stream().filter(
                                r -> r.getLocation().distance(player.getLocation()) <= ChatConfig.getChatWorldChatRange()
                        ).collect(Collectors.toList());
            } else {
                recipients =
                        recipients.stream().filter(
                                r -> r.getWorld().equals(player.getWorld())
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
