package eu.rex2go.chat2go.command.exception;

public class CustomErrorCommandException extends CommandException {

    public CustomErrorCommandException(String message, String... args) {
        super(message, args);
    }
}
