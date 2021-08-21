package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import eu.rex2go.chat2go.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends AbstractListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = Chat2Go.getUserManager().loadUser(player.getUniqueId(), player.getName(), true);

        if(!ChatConfig.isChatJoinMessageShow()) {
            event.setJoinMessage(null);
            return;
        }

        String format = ChatConfig.getChatJoinMessageFormat();

        format = Chat2Go.parseColor(format);

        Placeholder usernamePlaceholder = new Placeholder("username", TextComponent.fromLegacyText(user.getName()));
        Placeholder prefixPlaceholder = new Placeholder("prefix", TextComponent.fromLegacyText(user.getPrefix()));
        Placeholder suffixPlaceholder = new Placeholder("suffix", TextComponent.fromLegacyText(user.getSuffix()));

        BaseComponent[] components = PlaceholderProcessor.process(format, player, usernamePlaceholder, prefixPlaceholder, suffixPlaceholder);

        event.setJoinMessage(TextComponent.toLegacyText(components));
    }
}
