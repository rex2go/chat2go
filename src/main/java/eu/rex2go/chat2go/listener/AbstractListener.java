package eu.rex2go.chat2go.listener;

import eu.rex2go.chat2go.Chat2Go;
import org.bukkit.event.Listener;

public abstract class AbstractListener implements Listener {

    protected Chat2Go plugin;

    AbstractListener() {
        this.plugin = Chat2Go.getInstance();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
