package eu.rex2go.chat2go.user;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.ChatPermission;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.database.ConnectionWrapper;
import eu.rex2go.chat2go.database.DatabaseManager;
import eu.rex2go.chat2go.placeholder.Placeholder;
import eu.rex2go.chat2go.placeholder.PlaceholderProcessor;
import jdk.jfr.SettingDefinition;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

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
    private User lastChatter;

    private boolean inPrivateChat = false;

    @Getter
    private List<UUID> ignored = new ArrayList<>();

    @Getter
    private User spyTarget;

    @Getter
    @Setter
    private boolean spyCommands = false;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        // load mute
        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        if (connectionWrapper == null) return;

        this.mute = Chat2Go.getUserManager().loadMute(this, connectionWrapper.getConnection());
        Chat2Go.getUserManager().loadIgnoreList(this, connectionWrapper.getConnection());

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

    public String getDisplayName() {
        Player player = getPlayer();

        if (player != null) {
            return player.getDisplayName();
        }

        return name;
    }

    public void setInPrivateChat(boolean value) {
        if (getLastChatter() == null) return;

        if (value) {
            sendMessage("command.message.toggle_enabled", false, getLastChatter().getDisplayName());
        } else {
            sendMessage("command.message.toggle_disabled", false, getLastChatter().getDisplayName());
        }

        inPrivateChat = value;
    }

    public boolean isInPrivateChat() {
        if (inPrivateChat && getLastChatter() == null) {
            setInPrivateChat(false);
        }

        return inPrivateChat;
    }

    public void setLastChatter(User lastChatter) {
        if (inPrivateChat && lastChatter != this.lastChatter) {
            setInPrivateChat(false);
        }

        this.lastChatter = lastChatter;
    }

    public void say(String message) {
        AsyncPlayerChatEvent asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, getPlayer(), message, new HashSet<>(Bukkit.getOnlinePlayers()));
        Bukkit.getPluginManager().callEvent(asyncPlayerChatEvent);

        String formatted = String.format(asyncPlayerChatEvent.getFormat(), getDisplayName(), asyncPlayerChatEvent.getMessage());

        // show in log
        Chat2Go.getInstance().getLogger().log(Level.INFO, formatted);

        for (Player recipient : asyncPlayerChatEvent.getRecipients()) {
            recipient.sendMessage(formatted);
        }
    }


    public boolean ignore(User user) {
        if (ignored.contains(user.getUuid())) return false;

        ignored.add(user.getUuid());

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "INSERT INTO `ignore` (user_uuid, ignore_uuid) VALUES (?, ?)");

        try {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, user.getUuid().toString());

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        connectionWrapper.close();

        return true;
    }


    public boolean unignore(User user) {
        if (!ignored.contains(user.getUuid())) return false;

        ignored.remove(user.getUuid());

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "DELETE FROM `ignore` WHERE user_uuid = ? AND ignore_uuid = ?");

        try {
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, user.getUuid().toString());

            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        connectionWrapper.close();

        return true;
    }

    public void sendPrivateMessage(User sender, String message) {
        setLastChatter(sender);

        String formatTo = ChatConfig.getFormatPrivateMessageTo();
        String formatFrom = ChatConfig.getFormatPrivateMessageFrom();

        formatTo = Chat2Go.parseColor(formatTo);
        formatFrom = Chat2Go.parseColor(formatFrom);

        Placeholder senderPlaceholder = new Placeholder("sender", TextComponent.fromLegacyText(sender.getPlayer().getDisplayName()));
        Placeholder senderPrefixPlaceholder = new Placeholder("senderPrefix", TextComponent.fromLegacyText(sender.getPrefix()));
        Placeholder senderSuffixPlaceholder = new Placeholder("senderSuffix", TextComponent.fromLegacyText(sender.getSuffix()));
        Placeholder senderWorldPlaceholder = new Placeholder("senderWorld", TextComponent.fromLegacyText(sender.getPlayer().getWorld().getName()));
        Placeholder senderGroupPlaceholder = new Placeholder("senderGroup", TextComponent.fromLegacyText(sender.getPrimaryGroup()));

        Placeholder recipientPlaceholder = new Placeholder("recipient", TextComponent.fromLegacyText(getPlayer().getDisplayName()));
        Placeholder recipientPrefixPlaceholder = new Placeholder("recipientPrefix", TextComponent.fromLegacyText(getPrefix()));
        Placeholder recipientSuffixPlaceholder = new Placeholder("recipientSuffix", TextComponent.fromLegacyText(getSuffix()));
        Placeholder recipientWorldPlaceholder = new Placeholder("recipientWorld", TextComponent.fromLegacyText(getPlayer().getWorld().getName()));
        Placeholder recipientGroupPlaceholder = new Placeholder("recipientGroup", TextComponent.fromLegacyText(getPrimaryGroup()));

        Placeholder messagePlaceholder = new Placeholder("message", TextComponent.fromLegacyText(message));

        BaseComponent[] components = PlaceholderProcessor.process(
                formatTo,
                getPlayer(),
                false,
                senderPlaceholder,
                senderPrefixPlaceholder,
                senderSuffixPlaceholder,
                senderWorldPlaceholder,
                senderGroupPlaceholder,
                recipientPlaceholder,
                recipientPrefixPlaceholder,
                recipientSuffixPlaceholder,
                recipientWorldPlaceholder,
                recipientGroupPlaceholder,
                messagePlaceholder);

        BaseComponent[] componentsSender = PlaceholderProcessor.process(
                formatFrom,
                sender.getPlayer(),
                false,
                senderPlaceholder,
                senderPrefixPlaceholder,
                senderSuffixPlaceholder,
                senderWorldPlaceholder,
                senderGroupPlaceholder,
                recipientPlaceholder,
                recipientPrefixPlaceholder,
                recipientSuffixPlaceholder,
                recipientWorldPlaceholder,
                recipientGroupPlaceholder,
                messagePlaceholder);

        // spy msg
        for (User user : Chat2Go.getUserManager().getUsers()) {
            if(user.equals(this)) continue;
            if(user.equals(sender)) continue;
            if (user.getSpyTarget() == null) continue;
            if (!user.hasPermission(ChatPermission.COMMAND_SPY.getPermission())) continue;

            if (user.getSpyTarget().equals(sender)) {
                user.getPlayer().sendMessage("§6Spy (" + sender.getDisplayName() + "): §7" + TextComponent.toLegacyText(componentsSender));
                continue;
            }

            if (user.getSpyTarget().equals(this)) {
                user.getPlayer().sendMessage("§6Spy (" + getDisplayName() + "): §7" + TextComponent.toLegacyText(components));
                continue;
            }
        }

        sender.getPlayer().spigot().sendMessage(componentsSender);
        getPlayer().spigot().sendMessage(components);
    }

    public void setSpyTarget(User spyTarget) {
        if (spyTarget == null) {
            if (this.spyTarget != null) {
                sendMessage("command.spy.spying_disabled", false, this.spyTarget.getDisplayName());
            }

            this.spyCommands = false;
        } else {
            sendMessage("command.spy.spying_enabled", false, spyTarget.getDisplayName());
        }

        this.spyTarget = spyTarget;
    }
}
