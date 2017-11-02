package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author justintkj
/**
* Parses input arguments and creates a new RedoCommand object
*/
public class RedoCommandParser implements Parser<RedoCommand> {

    public static final String NUMBER_ONE = "1";

    /**
     * Parses the given {@code String} of arguments in the context of the UndoCommand
     * and returns an RedoCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public RedoCommand parse(String args) throws ParseException {
        String[] splitArgs = args.trim().split(" ");

        int numRedo;
        try {
            if (splitArgs[0].trim().equals("")) {
                numRedo = ParserUtil.parseNumber(NUMBER_ONE);
            } else {
                numRedo = ParserUtil.parseNumber(splitArgs[0]);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RedoCommand.MESSAGE_USAGE), ive);
        }

        return new RedoCommand(numRedo);
    }

}
