package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PlayerChatListener extends AbstractListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        User user = Chat2Go.getUser(player);

        if(user == null) {
            event.setCancelled(true);
            player.sendMessage("§cError");
            return;
        }

        // chat disabled
        if (!ChatConfig.isChatEnabled()) {
            event.setCancelled(true);
            // TODO message player
            return;
        }

        // TODO mute

        String message = event.getMessage();
        BaseComponent[] messageComponents = TextComponent.fromLegacyText(message); // TODO process message

        String chatFormat = ChatConfig.getChatFormatFormat();

        chatFormat = org.bukkit.ChatColor.translateAlternateColorCodes('&', chatFormat);
        chatFormat = Chat2Go.parseHexColor(chatFormat);

        BaseComponent[] usernameComponents = TextComponent.fromLegacyText(player.getName());
        for(BaseComponent usernameComponent : usernameComponents) {
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
