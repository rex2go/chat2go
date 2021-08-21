package eu.rex2go.chat2go;

public enum ChatPermission {

    BYPASS_FILTER,
    BYPASS_MUTE,
    BYPASS_ANTISPAM,
    CHAT_COLOR,
    CHAT_MENTION,
    COMMAND_BROADCAST,
    COMMAND_MSG,
    NOTIFY_FILTER;

    public String getPermission() {
        return "chat2go." + this.name().replace("_", ".").toLowerCase();
    }

}
