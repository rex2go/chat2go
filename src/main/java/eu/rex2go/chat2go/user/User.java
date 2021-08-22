package eu.rex2go.chat2go.user;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.database.ConnectionWrapper;
import eu.rex2go.chat2go.database.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
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

        // load mute
        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        if (connectionWrapper == null) return;

        this.mute = Chat2Go.getUserManager().loadMute(this, connectionWrapper.getConnection());

        connectionWrapper.close();
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

    public String getPrimaryGroup() {
        if (Chat2Go.isVaultInstalled()) {
            return Chat2Go.getChat().getPrimaryGroup(getPlayer());
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

        long seconds = (mute.getUnmuteTime() - mute.getTime()) / 1000;

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "REPLACE INTO `mute` VALUES (?, ?, ?, ?, ?)");

        try {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setLong(2, mute.getTime());
            preparedStatement.setLong(3, mute.getUnmuteTime());
            preparedStatement.setString(4, mute.getReason());
            preparedStatement.setString(5, mute.getMuter().toString());

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        connectionWrapper.close();

        if (mute.getReason() == null) {
            sendMessage("chat.you_have_been_muted", false, mute.getRemainingTimeString());
        } else {
            sendMessage("chat.you_have_been_muted_reason", false, mute.getRemainingTimeString(), mute.getReason());
        }
    }

    public boolean isMuted() {
        if (mute != null && mute.getUnmuteTime() <= System.currentTimeMillis()) {
            unmute();
        }

        return mute != null;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = System.currentTimeMillis();
    }

    public void unmute() {
        if (mute == null) return;

        mute = null;

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "DELETE FROM `mute` WHERE user_uuid = ?");

        try {
            preparedStatement.setString(1, uuid.toString());

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        connectionWrapper.close();
    }
}
