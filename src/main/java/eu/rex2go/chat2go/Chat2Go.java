package eu.rex2go.chat2go;

import eu.rex2go.chat2go.chat.ChatManager;
import eu.rex2go.chat2go.command.broadcast.BroadcastCommand;
import eu.rex2go.chat2go.command.chat.ChatCommand;
import eu.rex2go.chat2go.command.force.ForceCommand;
import eu.rex2go.chat2go.command.ignore.IgnoreCommand;
import eu.rex2go.chat2go.command.ignorelist.IgnoreListCommand;
import eu.rex2go.chat2go.command.msg.MsgCommand;
import eu.rex2go.chat2go.command.mute.MuteCommand;
import eu.rex2go.chat2go.command.reply.ReplyCommand;
import eu.rex2go.chat2go.command.unignore.UnignoreCommand;
import eu.rex2go.chat2go.command.unmute.UnmuteCommand;
import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.config.FilterConfig;
import eu.rex2go.chat2go.config.MessageConfig;
import eu.rex2go.chat2go.database.DatabaseManager;
import eu.rex2go.chat2go.listener.PlayerChatListener;
import eu.rex2go.chat2go.listener.PlayerJoinListener;
import eu.rex2go.chat2go.listener.PlayerQuitListener;
import eu.rex2go.chat2go.translator.Translator;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.user.UserManager;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat2Go extends JavaPlugin {

    public static final String PREFIX =
            ChatColor.WHITE + "[" + ChatColor.AQUA + "chat2go" + ChatColor.WHITE + "]" + ChatColor.GRAY;

    public static final String WARNING_PREFIX =
            ChatColor.RED + "[" + ChatColor.DARK_RED + "!" + ChatColor.RED + "]" + ChatColor.GRAY;

    @Getter
    private static Chat2Go instance;

    @Getter
    private static ChatConfig chatConfig;

    @Getter
    private static Translator translator;

    @Getter
    private static MessageConfig messageConfig;

    @Getter
    private static FilterConfig filterConfig;

    @Getter
    private static boolean vaultInstalled;

    @Getter
    private static boolean placeholderInstalled;

    @Getter
    private static Chat chat;

    @Getter
    private static ChatManager chatManager;

    @Getter
    private static DatabaseManager databaseManager;

    @Getter
    private static UserManager userManager;

    public static String parseColor(String str) {
        str = ChatColor.translateAlternateColorCodes('&', str);

        String[] patterns = new String[]{"&#(.{6}?)"};

        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(str);

            while (matcher.find()) {
                String hexMatch = matcher.group(0);
                String hex = hexMatch.replaceAll("[<>#()&]", "");
                hex = "#" + hex;

                try {
                    net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(hex);
                    str = str.replace(hexMatch, color.toString());
                } catch (Exception ignored) {
                }
            }
        }

        return str;
    }

    public static void sendMessage(CommandSender sender, String key, boolean prefix, String... args) {
        sender.sendMessage((prefix ? Chat2Go.PREFIX + " " : "") + translator.getTranslation(key, args));
    }

    public static User getUser(Player player) {
        return userManager.getUser(player);
    }

    @Override
    public void onEnable() {
        instance = this;

        // setup config
        chatConfig = new ChatConfig();
        chatConfig.load();

        // setup translator
        translator = new Translator();

        // setup message config
        messageConfig = new MessageConfig();
        messageConfig.load();

        // setup filter config
        filterConfig = new FilterConfig();
        filterConfig.load();

        checkDependencies();

        setupDatabase();
        setupTables();
        setupManagers();
        setupCommands();

        registerListener();

        if (ChatConfig.isGeneralStatisticsAllowed()) {
            new Metrics(this, 8164);
        }
    }

    private void checkDependencies() {
        PluginManager pm = Bukkit.getPluginManager();

        // check for vault
        if (vaultInstalled = pm.getPlugin("Vault") != null) {
            if (!setupChat()) {
                getLogger().log(Level.SEVERE, "Vault error. Disabling Vault integration.");
                vaultInstalled = false;
            }
        } else {
            getLogger().log(
                    Level.WARNING,
                    "Vault is not installed. There's a chance prefixes and suffixes won't be detected.");
        }

        // check for placeholder api
        if (placeholderInstalled = pm.getPlugin("PlaceholderAPI") != null) {
            getLogger().log(Level.INFO, "Placeholder API is installed.");
        }
    }

    private void registerListener() {
        new PlayerChatListener();
        new PlayerJoinListener();
        new PlayerQuitListener();
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> chatProvider =
                getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);

        if (chatProvider != null) {
            chat = chatProvider.getProvider();
            return true;
        }

        return false;
    }

    private void setupCommands() {
        new BroadcastCommand();
        new ChatCommand();
        new IgnoreCommand();
        new UnignoreCommand();
        new IgnoreListCommand();
        new ForceCommand();
        new MsgCommand();
        new MuteCommand();
        new ReplyCommand();
        new UnmuteCommand();
    }

    private void setupManagers() {
        chatManager = new ChatManager();
        userManager = new UserManager();
    }

    private void setupDatabase() {
        boolean useMySQL = ChatConfig.isDatabaseUseMySQL();

        if (useMySQL) {
            String host = ChatConfig.getDatabaseHost();
            int port = ChatConfig.getDatabasePort();
            String database = "chat2go";
            String user = ChatConfig.getDatabaseUser();
            String password = ChatConfig.getDatabasePassword();

            databaseManager = new DatabaseManager(host, database, user, password, port);
        } else {
            File databaseFile = new File(getDataFolder(), "chat2go.db");

            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException exception) {
                    getLogger().log(Level.SEVERE, "Failed to created SQLite database. Error: " + exception.getMessage());
                    getPluginLoader().disablePlugin(this);
                    return;
                }
            }

            databaseManager = new DatabaseManager(databaseFile);
        }
    }

    private void setupTables() {
        try {
            Connection connection = databaseManager.getDataSource().getConnection();
            PreparedStatement ps;

            if (databaseManager.isMySQL()) {
                ps = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS ?");
                ps.setString(1, databaseManager.getDatabase());

                ps.execute();
                ps.close();
            }

            // user table
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `user` ( uuid VARCHAR(32) NOT NULL, username VARCHAR(16) NOT NULL, time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (uuid) );");
            ps.execute();
            ps.close();

            // mute table
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `mute` ( user_uuid VARCHAR(32) NOT NULL, time BIGINT NOT NULL, unmuteTime BIGINT NOT NULL, reason VARCHAR(256), muter_uuid VARCHAR(32), PRIMARY KEY (user_uuid), FOREIGN KEY (user_uuid) REFERENCES user (uuid), FOREIGN KEY (muter_uuid) REFERENCES user (uuid) );");
            ps.execute();
            ps.close();

            // ignore table
            ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ignore` ( user_uuid VARCHAR(32) NOT NULL, ignore_uuid VARCHAR(32) NOT NULL, FOREIGN KEY (user_uuid) REFERENCES user(uuid), FOREIGN KEY (ignore_uuid) REFERENCES user(uuid) );");
            ps.execute();
            ps.close();

            connection.close();

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
