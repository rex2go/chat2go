package eu.rex2go.chat2go;

public enum ChatPermission {

    COMMAND_MSG,
    CHAT_COLOR,
    CHAT_MENTION,
    NOTIFY_FILTER;

    public String getPermission() {
        return "chat2go." + this.name().replace("_", ".").toLowerCase();
    }

}
