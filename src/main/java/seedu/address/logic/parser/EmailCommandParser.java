package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.EmailCommand;
import seedu.address.logic.parser.exceptions.ParseException;
//@@author justintkj
/**
 * Parses input arguments and creates a new EmailCommand object
 */
public class EmailCommandParser implements Parser<EmailCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EmailCommand
     * and returns a Email object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EmailCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Index index;
        String subject;
        String message;
        try {
            String[] messages = args.trim().split(",");
            if (messages.length != 3) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
            }
            String[] splitArgs = messages[0].trim().split(" ");
            index = ParserUtil.parseIndex(splitArgs[0]);
            subject = (messages[1]);
            message = (messages[2]);
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
        }
        return new EmailCommand(index, subject, message);
    }
}
