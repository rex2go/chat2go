package eu.rex2go.chat2go.config;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.customcomponent.CustomComponent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatConfig extends RexConfig {

    @Getter
    @ConfigInfo(path = "general.statisticsAllowed")
    private static boolean generalStatisticsAllowed;

    @Getter
    @ConfigInfo(path = "general.eventPriority")
    private static String generalEventPriority;

    @Getter
    @ConfigInfo(path = "database.useMySQL")
    private static boolean databaseUseMySQL;

    @Getter
    @ConfigInfo(path = "database.host")
    private static String databaseHost;

    @Getter
    @ConfigInfo(path = "database.port")
    private static int databasePort;

    @Getter
    @ConfigInfo(path = "database.user")
    private static String databaseUser;

    @Getter
    @ConfigInfo(path = "database.password")
    private static String databasePassword;

    @Getter
    @ConfigInfo(path = "chat.enabled")
    private static boolean chatEnabled;

    @Getter
    @ConfigInfo(path = "chat.format.format")
    private static String chatFormatFormat;

    @Getter
    private static Map<String, String> chatFormatGroupFormats;

    @Getter
    @ConfigInfo(path = "chat.format.translateChatColors")
    private static boolean chatFormatTranslateChatColors;

    @Getter
    @ConfigInfo(path = "chat.joinMessage.show")
    private static boolean chatJoinMessageShow;

    @Getter
    @ConfigInfo(path = "chat.joinMessage.format")
    private static String chatJoinMessageFormat;

    @Getter
    @ConfigInfo(path = "chat.quitMessage.show")
    private static boolean chatQuitMessageShow;

    @Getter
    @ConfigInfo(path = "chat.quitMessage.format")
    private static String chatQuitMessageFormat;

    @Getter
    @ConfigInfo(path = "chat.worldChat.enabled")
    private static boolean chatWorldChatEnabled;

    @Getter
    @ConfigInfo(path = "chat.worldChat.considerRange")
    private static boolean worldChatConsiderRange;

    @Getter
    @ConfigInfo(path = "chat.worldChat.range")
    private static int chatWorldChatRange;

    @Getter
    @ConfigInfo(path = "filter.enabled")
    private static boolean filterEnabled;

    @Getter
    @ConfigInfo(path = "filter.filterMode")
    private static String filterFilterMode;

    @Getter
    private static List<Pattern> filterWhitelist;

    @Getter
    @ConfigInfo(path = "antiSpam.enabled")
    private static boolean antiSpamEnabled;

    @Getter
    @ConfigInfo(path = "antiSpam.capsThreshold")
    private static double antiSpamCapsThreshold;

    @Getter
    @ConfigInfo(path = "antiSpam.spaceThreshold")
    private static double antiSpamSpaceThreshold;

    @Getter
    @ConfigInfo(path = "antiSpam.maxCharRepetitions")
    private static int antiSpamMaxCharRepetitions;

    @Getter
    @ConfigInfo(path = "notification.filter.enabled")
    private static boolean notificationFilterEnabled;

    @Getter
    @ConfigInfo(path = "format.privateMessage")
    private static String formatPrivateMessage;

    @Getter
    @ConfigInfo(path = "format.broadcast")
    private static String formatBroadcast;

    @Getter
    @ConfigInfo(path = "customComponents.enabled")
    private static boolean customComponentsEnabled;

    @Getter
    private static Map<String, CustomComponent> customComponents;

    public ChatConfig() {
        super(Chat2Go.getInstance(), "config.yml", 7);
    }

    public static boolean useCompatibilityMode() {
        return !(chatWorldChatEnabled || customComponentsEnabled);
    }

    @Override
    public void load() {
        super.load();

        chatFormatGroupFormats = new HashMap<>();

        if(getConfig().isConfigurationSection("chat.format.groupFormats")) {
            getConfig().getConfigurationSection("chat.format.groupFormats").getKeys(false).forEach(id -> {
                String groupFormat = getConfig().getString("chat.format.groupFormats." + id);
                chatFormatGroupFormats.put(id, groupFormat);
            });
        }

        filterWhitelist = new ArrayList<>();

        List<String> patterns = getConfig().getStringList("filter.whitelist");

        for (String pattern : patterns) {
            filterWhitelist.add(Pattern.compile(pattern));
        }

        customComponents = new HashMap<>();

        if (customComponentsEnabled) {
            getConfig().getConfigurationSection("customComponents.components").getKeys(false).forEach(id -> {
                String text = getConfig().getString("customComponents.components." + id + ".text");
                String hoverText = getConfig().getString("customComponents.components." + id + ".hoverText");
                String suggestCommand = getConfig().getString("customComponents.components." + id + ".suggestCommand");
                String runCommand = getConfig().getString("customComponents.components." + id + ".runCommand");
                String openUrl = getConfig().getString("customComponents.components." + id + ".openUrl");

                CustomComponent customComponent = new CustomComponent(text);
                customComponent.setHoverText(hoverText);
                customComponent.setSuggestCommand(suggestCommand);
                customComponent.setRunCommand(runCommand);
                customComponent.setOpenUrl(openUrl);

                customComponents.put(id, customComponent);
            });
        }
    }
}
