package eu.rex2go.chat2go.command.exception;

public class PlayerNotOnlineCommandException extends CommandException {

    public PlayerNotOnlineCommandException(String playerName) {
        super("command.error.player_not_online", playerName);
    }
}
