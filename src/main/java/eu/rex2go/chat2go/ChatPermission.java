package eu.rex2go.chat2go;

public enum ChatPermission {

    COMMAND_MSG,

    CHAT_MENTION;

    public String getPermission() {
        return this.name().replace("_", ".").toLowerCase();
    }

}
