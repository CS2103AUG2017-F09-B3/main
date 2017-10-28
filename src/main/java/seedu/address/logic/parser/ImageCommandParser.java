package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.ImageCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ImageCommand object
 */
public class ImageCommandParser implements Parser<ImageCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ImageCommand
     * and returns an ImageCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public ImageCommand parse(String args) throws ParseException {
        String[] splitArgs = args.trim().split(" ");
        try {
            Index index = ParserUtil.parseIndex(splitArgs[0]);
            if (splitArgs.length > 1 && splitArgs[1].equals("remove")) {
                return new ImageCommand(index, true);
            } else {
                return new ImageCommand(index, false);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImageCommand.MESSAGE_USAGE));
        }
    }
}
