package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.user.UserManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends AbstractListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UserManager userManager = Chat2Go.getUserManager();
        Player player = event.getPlayer();
        User user = userManager.getUser(player);

        if (user == null) return;

        for (User users : userManager.getUsers()) {
            if (users.getLastChatter() == null) continue;
            if (users.getLastChatter().getUuid().equals(user.getUuid())) {
                users.setLastChatter(null);
            }
        }

        userManager.saveUser(user);
        userManager.getUsers().remove(user);

        if (!ChatConfig.isChatQuitMessageShow()) {
            event.setQuitMessage(null);
            return;
        }

        String format = ChatConfig.getChatQuitMessageFormat();

        format = Chat2Go.parseColor(format);

        Placeholder usernamePlaceholder = new Placeholder("username", TextComponent.fromLegacyText(user.getDisplayName()));
        Placeholder prefixPlaceholder = new Placeholder("prefix", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder suffixPlaceholder = new Placeholder("suffix", TextComponent.fromLegacyText(user.getSuffix()));
        Placeholder worldPlaceholder = new Placeholder("world", TextComponent.fromLegacyText(player.getWorld().getName()));
        Placeholder groupPlaceholder = new Placeholder("group", TextComponent.fromLegacyText(user.getPrimaryGroup()));

        BaseComponent[] components = PlaceholderProcessor.process(
                format,
                player,
                false,
                usernamePlaceholder,
                prefixPlaceholder,
                suffixPlaceholder,
                worldPlaceholder,
                groupPlaceholder
        );

        event.setQuitMessage(TextComponent.toLegacyText(components));
    }
}
