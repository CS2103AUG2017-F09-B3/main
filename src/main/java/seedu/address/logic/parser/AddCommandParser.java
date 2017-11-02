package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BIRTHDAY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Birthday;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favourite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ProfilePicture;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.Remark;
import seedu.address.model.tag.Tag;

//@@author justintkj
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                        PREFIX_REMARK, PREFIX_BIRTHDAY);


        try {
            ProfilePicture picture = new ProfilePicture("default");
            if (args.contains(PREFIX_NAME.toString()) || args.contains(PREFIX_ADDRESS.toString())
                || args.contains(PREFIX_EMAIL.toString()) || args.contains(PREFIX_PHONE.toString())
                || args.contains(PREFIX_REMARK.toString()) || args.contains(PREFIX_TAG.toString())) {
                if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
                }
                Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
                Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).get();
                Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).get();
                Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).get();
                Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK)).get();
                Birthday birthday = ParserUtil.parseBirthday(argMultimap.getValue(PREFIX_BIRTHDAY)).get();
                Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
                Favourite favourite = new Favourite("false");
                ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                        favourite);
                return new AddCommand(person);
            } else {
                Remark remark = new Remark("");
                Birthday birthday = new Birthday("");
                String[] allArgs = args.split(",");
                if (allArgs.length < 2) {
                    throw new IllegalValueException("Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
                }
                Name name = new Name(allArgs[0]);
                Email email;
                Phone phone;
                String blocknum;
                String streetnum;
                String unitnum;
                String postalnum = "";
                Favourite favourite = new Favourite("false");

                Pattern emailpattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
                Matcher matcher = emailpattern.matcher(args);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    email = new Email(matcher.group(0));
                } else {
                    throw new IllegalValueException("invalid email\n Example: Jason@example.com");
                }
                Pattern block = Pattern.compile("block \\d{1,3}", Pattern.CASE_INSENSITIVE);
                matcher = block.matcher(args);
                Pattern blk = Pattern.compile("blk \\d{1,3}", Pattern.CASE_INSENSITIVE);
                Matcher blkmatcher = blk.matcher(args);
                matchFound = matcher.find();
                if (!matchFound) {
                    matchFound = blkmatcher.find();
                    matcher = blkmatcher;
                }
                if (matchFound) {
                    blocknum = matcher.group(0);
                } else {
                    throw new IllegalValueException("invalid address, Block Number. \nExample: Block 123"
                        + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern street = Pattern.compile("[a-zA-z]+ street \\d{1,2}", Pattern.CASE_INSENSITIVE);
                matcher = street.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    streetnum = matcher.group(0);
                } else {
                    throw new IllegalValueException("invalid address, Street. \nExample: Jurong Street 11"
                        + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern unit = Pattern.compile("#\\d\\d-\\d{1,3}[a-zA-Z]{0,1}", Pattern.CASE_INSENSITIVE);
                matcher = unit.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    unitnum = matcher.group(0);
                } else {
                    throw new IllegalValueException("invalid address, Unit. \n Example: #01-12B"
                        + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern postal = Pattern.compile("singapore \\d{6,6}", Pattern.CASE_INSENSITIVE);
                matcher = postal.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    postalnum = ", " + matcher.group(0);
                }
                Pattern phonepattern = Pattern.compile("\\ {0,1}\\d{8}\\ {0,1}");
                matcher = phonepattern.matcher(args);
                matchFound = matcher.find();
                if (!matchFound) {
                    phonepattern = Pattern.compile("\\,{0,1}\\d{8}\\,{0,1}");
                    matcher = phonepattern.matcher(args);
                    matchFound = matcher.find();
                }
                if (matchFound) {
                    phone = new Phone(matcher.group(0).trim().replace(",", ""));
                } else {
                    throw new IllegalValueException("Number should be 8 digits long!\n" + AddCommand.MESSAGE_USAGE_ALT);
                }
                Pattern birthpattern = Pattern.compile("\\d{1,2}-\\d{1,2}-\\d{4,4}", Pattern.CASE_INSENSITIVE);
                matcher = birthpattern.matcher(args);
                matchFound = matcher.find();
                if (matchFound) {
                    if (Birthday.isValidBirthday(matcher.group(0))) {
                        birthday = new Birthday(matcher.group(0));
                    } else {
                        throw new IllegalValueException("invalid birthday,\n Example: 12-09-1994");
                    }
                }

                Address address = new Address(blocknum + ", " + streetnum + ", " + unitnum + postalnum);
                Set<Tag> tagList = new HashSet<>();
                ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                        favourite);
                return new AddCommand(person);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }
    //@@author
    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
