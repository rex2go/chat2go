package eu.rex2go.chat2go.command.exception;

public class PlayerNotFoundCommandException extends CommandException {

    public PlayerNotFoundCommandException(String playerName) {
        super("command.error.player_not_found", playerName);
    }
}
