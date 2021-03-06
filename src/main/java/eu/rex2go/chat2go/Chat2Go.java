package eu.rex2go.chat2go;

import eu.rex2go.chat2go.chat.ChatManager;
import eu.rex2go.chat2go.command.*;
import eu.rex2go.chat2go.config.*;
import eu.rex2go.chat2go.listener.PlayerChatListener;
import eu.rex2go.chat2go.listener.PlayerJoinListener;
import eu.rex2go.chat2go.listener.PlayerQuitListener;
import eu.rex2go.chat2go.task.MainTask;
import eu.rex2go.chat2go.user.User;
import eu.rex2go.chat2go.user.UserManager;
import eu.rex2go.chat2go.util.MathUtil;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat2Go extends JavaPlugin {

    public static final String PREFIX =
            ChatColor.WHITE + "[" + ChatColor.AQUA + "chat2go" + ChatColor.WHITE + "]" + ChatColor.GRAY;

    public static final String WARNING_PREFIX =
            ChatColor.RED + "[" + ChatColor.DARK_RED + "!" + ChatColor.RED + "]" + ChatColor.GRAY;

    @Getter
    private static Chat chat;

    @Getter
    @Setter
    private static boolean vaultInstalled, hexSupported, placeholderInstalled = false;

    @Getter
    private static MessageConfig messageConfig;

    @Getter
    private static BadWordConfig badWordConfig;

    @Getter
    private static MainConfig mainConfig;

    @Getter
    private static LinkWhitelistConfig linkWhitelistConfig;

    @Getter
    private static AutoBroadcastConfig autoBroadcastConfig;

    @Getter
    private UserManager userManager;

    @Getter
    private ChatManager chatManager;

    public static void sendMessage(CommandSender sender, String key, boolean prefix, String... args) {
        sender.sendMessage((prefix ? Chat2Go.PREFIX + " " : "") + Chat2Go.getMessageConfig().getMessage(key, args));
    }

    public static String parseHexColor(String str) {
        if (Chat2Go.isHexSupported()) {
            String[] patterns = new String[]{"<#(.{6}?)>", "#\\((.{6}?)\\)", "&#(.{6}?)"};

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
        }

        return str;
    }

    @Override
    public void onEnable() {
        setupDependencies();

        String[] versionParts = getServer().getBukkitVersion().split("\\.");
        if (MathUtil.isNumber(versionParts[0]) && MathUtil.isNumber(versionParts[1])) {
            int v1 = Integer.parseInt(versionParts[0]);
            int v2 = Integer.parseInt(versionParts[1]);

            if (v1 >= 1 && v2 >= 16) {
                hexSupported = true;

                getLogger().log(Level.INFO, "Hex color is supported on this server.");
            }
        }

        setupConfigs();
        setupManagers();
        setupCommands();
        setupListeners();
        setupTasks();

        if (mainConfig.isStatisticsAllowed()) {
            new Metrics(this, 8164);
        }

        for (Player all : Bukkit.getOnlinePlayers()) {
            User user = new User(all);
            getUserManager().getUsers().add(user);
        }
    }

    private void setupDependencies() {
        PluginManager pm = Bukkit.getPluginManager();

        if (vaultInstalled = pm.getPlugin("Vault") != null) {
            if(!setupChat()) {
                getLogger().log(Level.SEVERE, "Vault error. Disabling Vault integration.");
                vaultInstalled = false;
            }
        } else {
            getLogger().log(Level.WARNING, "Vault is not installed. There's a chance prefixes and suffixes won't be " +
                    "detected.");
        }

        if (placeholderInstalled = pm.getPlugin("PlaceholderAPI") != null) {
            getLogger().log(Level.INFO, "Placeholder API is installed.");
        }
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

    private void setupConfigs() {
        mainConfig = new MainConfig(this);
        mainConfig.load();

        messageConfig = new MessageConfig(this);
        messageConfig.load();

        badWordConfig = new BadWordConfig(this);
        badWordConfig.load();

        linkWhitelistConfig = new LinkWhitelistConfig(this);
        linkWhitelistConfig.load();

        autoBroadcastConfig = new AutoBroadcastConfig(this);
        autoBroadcastConfig.load();
    }

    private void setupManagers() {
        userManager = new UserManager();
        chatManager = new ChatManager(this);
    }

    private void setupCommands() {
        new AutoBroadcastCommand(this);
        new BroadcastCommand(this);
        new ClearChatCommand(this);
        new Chat2GoCommand(this);
        new MessageCommand(this);
        new ReplyCommand(this);
        new SlowModeCommand(this);
    }

    private void setupListeners() {
        new PlayerChatListener(this);
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
    }

    private void setupTasks() {
        new MainTask(this).runTaskTimer(this, 20, 20);
    }
}
