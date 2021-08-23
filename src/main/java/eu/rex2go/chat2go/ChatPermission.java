package eu.rex2go.chat2go;

public enum ChatPermission {

    BYPASS_FILTER,
    BYPASS_MUTE,
    BYPASS_ANTISPAM,
    CHAT_COLOR,
    COMMAND_BROADCAST,
    COMMAND_MSG,
    COMMAND_CHAT,
    COMMAND_CHAT_RELOAD,
    COMMAND_CHAT_CLEAR,
    COMMAND_CHAT_BADWORD,
    COMMAND_FORCE,
    COMMAND_MUTE,
    NOTIFY_FILTER;

    public String getPermission() {
        return "chat2go." + this.name().replace("_", ".").toLowerCase();
    }

}
