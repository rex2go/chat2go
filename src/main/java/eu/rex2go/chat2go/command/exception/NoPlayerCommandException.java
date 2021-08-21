package eu.rex2go.chat2go.command.exception;

public class NoPlayerCommandException extends CommandException {

    public NoPlayerCommandException() {
        super("command.error.no_player");
    }
}
