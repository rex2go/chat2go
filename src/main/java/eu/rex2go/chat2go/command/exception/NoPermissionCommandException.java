package eu.rex2go.chat2go.command.exception;

public class NoPermissionCommandException extends CommandException {

    public NoPermissionCommandException(String permission) {
        super("command.error.error.no_permission", permission);
    }
}
