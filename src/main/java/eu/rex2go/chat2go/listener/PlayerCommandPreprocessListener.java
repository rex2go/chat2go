package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener extends AbstractListener {

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        User user = Chat2Go.getUser(player);

        if (user == null) return;

        for (User spy : Chat2Go.getUserManager().getUsers()) {
            if (spy.getSpyTarget() == null) continue;
            if (!spy.hasPermission(ChatPermission.COMMAND_SPY.getPermission())) continue;
            if(!spy.isSpyCommands()) continue;

            spy.getPlayer().sendMessage("§6Spy (" + user.getDisplayName() + "): §7" + event.getMessage());
        }
    }
}
