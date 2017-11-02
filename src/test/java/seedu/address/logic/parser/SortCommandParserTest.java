package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.Test;

import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.parser.exceptions.ParseException;

//@@author justintkj
/**
 * Tests for all possible type of arguments possible for sortCommand.
 */
public class SortCommandParserTest {
    private SortCommandParser parser = new SortCommandParser();

    //Tests for valid argument, sort number
    @Test
    public void parseNumber_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("number");
            if (newCommand.equals(new SortCommand("number"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort name
    @Test
    public void parseName_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("name");
            if (newCommand.equals(new SortCommand("name"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort address
    @Test
    public void parseAddress_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("address");
            if (newCommand.equals(new SortCommand("address"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort email
    @Test
    public void parseEmail_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("email");
            if (newCommand.equals(new SortCommand("email"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort birthday
    @Test
    public void parseBirthday_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("birthday");
            if (newCommand.equals(new SortCommand("birthday"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort remark
    @Test
    public void parseRemark_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("remark");
            if (newCommand.equals(new SortCommand("remark"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }
    //Tests for valid argument, sort number with CamelCase
    @Test
    public void parseCamelCase_returnsSortCommand() {
        try {
            SortCommand newCommand = parser.parse("NuMbEr");
            if (newCommand.equals(new SortCommand("number"))) {
                assert true;
            } else {
                assert false;
            }
        } catch (ParseException pe) {
            assert false;
        }
    }

    //Tests for invalid Argument
    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    //Tests for invalid Argument , Empty
    @Test
    public void parse_emptyInvalidArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
    }

    //Tests for invalid Argument , multiple valid argument
    @Test
    public void parse_multipleInvalidArgs_throwsParseException() {
        assertParseFailure(parser, "number name", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                SortCommand.MESSAGE_USAGE));
    }
}
