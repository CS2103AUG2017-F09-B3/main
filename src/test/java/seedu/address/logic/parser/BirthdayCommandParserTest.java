package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.BIRTHDAY_BOB;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.Test;

import seedu.address.logic.commands.BirthdayCommand;

//@@author liliwei25
/**
 * Test BirthdayCommandParser
 */
public class BirthdayCommandParserTest {

    private BirthdayCommandParser parser = new BirthdayCommandParser();

    @Test
    public void parse_validArgs_returnsBirthdayCommand() {
        assertParseSuccess(parser, " 1 " + "30-03-2002",
                new BirthdayCommand(INDEX_FIRST_PERSON, BIRTHDAY_BOB));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                BirthdayCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid birthday
        assertParseFailure(parser, BirthdayCommand.COMMAND_WORD + " 1 " + "0000",
                "Invalid command format! \n" + BirthdayCommand.MESSAGE_USAGE);

        // invalid index
        assertParseFailure(parser, BirthdayCommand.COMMAND_WORD + " -1 " + "12-12-2012",
                "Invalid command format! \n" + BirthdayCommand.MESSAGE_USAGE);
    }
}
