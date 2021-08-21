package eu.rex2go.chat2go.config;

import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;

public class ChatConfig extends RexConfig {

    @Getter
    @ConfigInfo(path = "general.statisticsAllowed")
    private static boolean generalStatisticsAllowed;

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

    // TODO

    @Getter
    @ConfigInfo(path = "chat.worldChat.enabled")
    private static boolean chatWorldChatEnabled;

    @Getter
    @ConfigInfo(path = "chat.worldChat.considerRange")
    private static boolean worldChatConsiderRange;

    @Getter
    @ConfigInfo(path = "chat.worldChat.range")
    private static int chatWorldChatRange;

    // TODO

    @Getter
    @ConfigInfo(path = "filter.enabled")
    private static boolean filterEnabled;

    @Getter
    @ConfigInfo(path = "filter.filterMode")
    private static String filterFilterMode;


    // anti spam

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


    // TODO

    @Getter
    @ConfigInfo(path = "notification.filter.enabled")
    private static boolean notificationFilterEnabled;

    // TODO

    public ChatConfig() {
        super(Chat2Go.getInstance(), "config.yml", 6);
    }

    public static boolean useCompatibilityMode() {
        return !(chatWorldChatEnabled);
    }
}
