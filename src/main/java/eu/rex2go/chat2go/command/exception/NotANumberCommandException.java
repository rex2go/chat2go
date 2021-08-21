package eu.rex2go.chat2go.command.exception;

public class NotANumberCommandException extends CommandException {

    public NotANumberCommandException(String number) {
        super("command.error.not_a_number", number);
    }
}
