package eu.rex2go.chat2go.user;

import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class User {

    @Getter
    private final UUID uuid;

    @Getter
    private String name;

    @Getter
    private Mute mute;

    @Getter
    private String lastMessage = null;

    @Getter
    private long lastMessageTime = 0;

    @Getter
    @Setter
    private User lastChatter;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public User(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getPrefix() {
        if (Chat2Go.isVaultInstalled()) {
            String prefix = Chat2Go.getChat().getPlayerPrefix(getPlayer());

            if (prefix == null) return "";

            prefix = Chat2Go.parseColor(prefix);

            return prefix;
        }

        return "";
    }

    public String getSuffix() {
        if (Chat2Go.isVaultInstalled()) {
            String suffix = Chat2Go.getChat().getPlayerSuffix(getPlayer());

            if (suffix == null) return "";

            suffix = Chat2Go.parseColor(suffix);

            return suffix;
        }

        return "";
    }

    public void sendMessage(String key, boolean prefix, String... args) {
        if (getPlayer() == null) return;

        Chat2Go.sendMessage(getPlayer(), key, prefix, args);
    }

    public boolean hasPermission(String... permissions) {
        Player player = getPlayer();
        return Arrays.stream(permissions).anyMatch(player::hasPermission);
    }

    public void setMute(Mute mute) {
        this.mute = mute;

        if (mute == null) {
            unmute();
            return;
        }

        long diff = mute.getUnmuteTime() - System.currentTimeMillis();

        // TODO update db

        if (mute.getReason() == null) {
            sendMessage("chat.you_have_been_muted", false, String.valueOf(diff));
        } else {
            sendMessage("chat.you_have_been_muted_message", false, String.valueOf(diff), mute.getReason());
        }
    }

    public boolean isMuted() {
        if (mute != null && mute.getUnmuteTime() >= System.currentTimeMillis()) {
            unmute();
        }

        return mute != null;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = System.currentTimeMillis();
    }

    public void unmute() {
        mute = null;
        // TODO update db
    }
}
