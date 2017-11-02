package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EmailCommand;

//@@author justintkj
/**
 * Tests for all valid Email types of command.
 */
public class EmailCommandParserTest {

    private EmailCommandParser parser = new EmailCommandParser();

    @Test
    public void parse_validArgs_returnsEmailCommand() {
        //VALID EMAIL FORMAT
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + "," + "subject" + "," + "body";
        EmailCommand expectedCommand = new EmailCommand(INDEX_FIRST_PERSON, "subject", "body");
        assertParseSuccess(parser, userInput, expectedCommand);

        //VALID EMAIL FORMAT - extra whitespaces
        targetIndex = INDEX_FIRST_PERSON;
        userInput = " " + targetIndex.getOneBased() + "  ," + "subject" + "," + "body";
        expectedCommand = new EmailCommand(INDEX_FIRST_PERSON, "subject", "body");
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        //No Index selected
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));

        //Too many inputs
        assertParseFailure(parser, "1,subject,body,extra", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));

        //Too little input
        assertParseFailure(parser, "1,subject", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                EmailCommand.MESSAGE_USAGE));
    }
}
