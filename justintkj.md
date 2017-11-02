# justintkj
###### \main\java\seedu\address\logic\commands\CommandUtil.java
``` java
/**
 * Contains utility methods used for common used command in the various *Command classes.
*/
public class CommandUtil {

    /**
     * Validates current index is smaller than total number of persons
     * @param lastShownList List of ReadOnlyPerson
     * @throws CommandException index is higher than total number of person.(zero based)
     */
    public static void checksIndexSmallerThanList(List<ReadOnlyPerson> lastShownList, Index index) throws CommandException {
        assert lastShownList != null;
        assert index != null;
        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }
}
```
###### \main\java\seedu\address\logic\commands\EmailCommand.java
``` java
/**
 * Email a person chosen by index
 * API usage referenced from https://www.javatpoint.com/example-of-sending-email-using-java-mail-api
 */
public class EmailCommand extends Command {

    public static final String COMMAND_WORD = "email";
    public static final String COMMAND_ALIAS = "e";

    public static final String MESSAGE_SUCCESS = "Email sent to :";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Email the person identified "
            + "by the index number used in the last person listing. "
            + "An email will be sent to the chosen person.\n"
            + "Parameters: INDEX (must be a positive integer),SUBJECT,MESSAGE "
            + "[Email Index,Subject,Message]\n"
            + "Example: " + COMMAND_WORD + " 1"
            + ",subjectmessage,textmessage";
    public static final String INCORRECT_EMAIL_FORMAT = "Incorrect Email format";
    public static final String CORRET_EMAIL_FORMAT = "Email successfully sent to : ";
    public final Index index;
    public final String subject;
    public final String message;

    private String host;
    private String user;
    private String pass;
    private String to;
    private String from;

    private Session mailSession;
    private Message msg;

    public EmailCommand(Index index, String subject, String message) {
        requireNonNull(index);
        requireNonNull(subject);
        requireNonNull(message);
        this.index = index;
        this.subject = subject;
        this.message = message;
    }

    @Override
    public CommandResult execute() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        CommandUtil.checksIndexSmallerThanList(lastShownList, index);

        ReadOnlyPerson personToEmail = lastShownList.get(index.getZeroBased());

        try {
            //Initial Login credentials
            generateInitialLoginCred(personToEmail);

            //Adds signature to user's text
            String messageText = teamSignatureGenerator();
            boolean sessionDebug = false;

            Properties props = System.getProperties();

            //updates the properties of the session
            updateProperties(host, props);

            //Compose the message
            composeMethod(messageText, sessionDebug, props);

            //Sends the Message
            sendsMessage(host, user, pass, mailSession, msg);

        } catch (Exception ex) {
            throw new CommandException(INCORRECT_EMAIL_FORMAT);
        }
        return new CommandResult(CORRET_EMAIL_FORMAT + personToEmail.getName());
    }

    /**
     * Compose the Email object
     * @param messageText The message to be sent out
     * @param sessionDebug
     * @param props
     * @throws MessagingException
     */
    private void composeMethod(String messageText, boolean sessionDebug, Properties props) throws MessagingException {
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        mailSession = Session.getDefaultInstance(props, null);
        mailSession.setDebug(sessionDebug);
        msg = composeMessage(to, from, subject, messageText, mailSession);
    }

    /**
     *  Initialises basic sending credentials
     * @param personToEmail Person to recieve the email
     */
    private void generateInitialLoginCred(ReadOnlyPerson personToEmail) {
        host = "smtp.gmail.com";
        user = "cs2103f09b3@gmail.com";
        pass = "pocketbook";
        to = personToEmail.getEmail().toString();
        from = "cs2103f09b3@gmail.com";
    }

    /**
     * Updates initial properties of javaMail
     * @param host DNS name of a server
     * @param props Properties of javamail API
     */
    private void updateProperties(String host, Properties props) {
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.required", "true");
    }

    /**
     * Sends the message to the email chosen
     * @param host DNS name of a server
     * @param user Email of CS2103F09B3 Team
     * @param pass Password of CS2103F09B3 Team
     * @param mailSession Contains properties and defaults used by Mail API
     * @param msg Message to be sent out
     * @throws MessagingException Invalid parameters used
     */
    private void sendsMessage(String host, String user, String pass, Session mailSession, Message msg) throws MessagingException {
        assert host == "smtp.gmail.com";
        assert user == "cs2103f09b3@gmail.com";
        assert pass == "pocketbook";
        Transport transport = mailSession.getTransport("smtp");
        transport.connect(host, user, pass);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    /**
     *
     * @param to Email of person to send
     * @param from Email of Sender
     * @param subject Message to be attached in subject of Email
     * @param messageText Message to be attached in body of Email
     * @param mailSession Contains properties and defaults used by Mail API
     * @return Message compressed into Message objet
     * @throws MessagingException Invalid parameters used in composing Email
     */
    private Message composeMessage(String to, String from, String subject, String messageText, Session mailSession) throws MessagingException {
        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(from));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(messageText);
        return msg;
    }

    /**
     * Creates the signature at the end of the email provided by CS2103F03B3 Team
     * @return New message attached with signature
     */
    private String teamSignatureGenerator() {
        String newLine = "";
        for (int i = 0; i < 5; i++) {
            newLine += System.getProperty("line.separator");
        }
        return this.message + newLine + "This is a generated mail provided by CS2103F09B3 Team.";
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof EmailCommand)) {
            return false;
        }

        // state check
        EmailCommand e = (EmailCommand) other;
        return index.equals(e.index) && subject.equals(e.subject) && message.equals(e.message);
    }
}
```
###### \main\java\seedu\address\logic\commands\FavouriteCommand.java
``` java
/**
 * Favourites a person identified using it's last displayed index from the address book.
 */
public class FavouriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "favourite";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Favourites/Highlights the person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_FAVOURITE_SUCCESS = "Favourite Person: %1$s";

    private final Index targetIndex;
    private final Favourite favourite;

    public FavouriteCommand(Index targetIndex, Favourite favourite) {
        requireNonNull(targetIndex);
        this.targetIndex = targetIndex;
        this.favourite = favourite;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        CommandUtil.checksIndexSmallerThanList(lastShownList, targetIndex);

        ReadOnlyPerson personToEdit = lastShownList.get(targetIndex.getZeroBased());
        ReadOnlyPerson editedPerson = personToEdit;
        toggleColor(editedPerson);
        editedPerson.setFavourite(favourite);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_FAVOURITE_SUCCESS, editedPerson));

    }

    /**
     * Toggles the current color state of the person selected
     * @param editedPerson selected person to edit
     */
    private void toggleColor(ReadOnlyPerson editedPerson) {
        if (editedPerson.getFavourite().toString().equals("true")) {
            favourite.inverse();
        }
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof FavouriteCommand)) {
            return false;
        }

        // state check
        FavouriteCommand e = (FavouriteCommand) other;
        return targetIndex.equals(e.targetIndex) && favourite.equals(e.favourite);
    }
}
```
###### \main\java\seedu\address\logic\commands\RedoCommand.java
``` java
    public RedoCommand(int numRedo) {
        requireNonNull(numRedo);
        this.numRedo = numRedo;
    }
    public RedoCommand() throws IllegalValueException {
        this.numRedo = ParserUtil.parseNumber(INDEX_ONE);
    }
    @Override
    public CommandResult execute() throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        checksStackNotEmpty();
        checksRedoSizeNotBiggerThanStack();

        redoMultipleTimes();

        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Redo for numRedo number of times
     * @throws CommandException if redo while stack is empty
     */
    private void redoMultipleTimes() throws CommandException {
        for (int i = INDEX_ZERO; i < numRedo; i++) {
            if (!undoRedoStack.canRedo()) {
                throw new CommandException(MESSAGE_FAILURE);
            }

            undoRedoStack.popRedo().redo();
        }
    }

    /**
     * Checks if number of redos is not bigger than current avaliable number of redos
     * @throws CommandException if redo while stack is empty
     */
    private void checksRedoSizeNotBiggerThanStack() throws CommandException {
        if (numRedo > undoRedoStack.getRedoStackSize()) {
            throw new CommandException(TOO_MANY_REDO_FORMAT + undoRedoStack.getRedoStackSize());
        }
    }

    /**
     * Checks if current number of redo avaliable is zero
     * @throws CommandException if current stack size is zero
     */
    private void checksStackNotEmpty() throws CommandException {
        if (undoRedoStack.getRedoStackSize() == 0) {
            throw new CommandException(EMPTY_STACK_ERROR_MESSAGE);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RedoCommand // instanceof handles nulls
                && this.numRedo == (((RedoCommand) other).numRedo)); // state check
    }
```
###### \main\java\seedu\address\logic\commands\RemarkCommand.java
``` java
/**
 * Edits the remark of the person identified by index number in person listing.
 */

public class RemarkCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the person identified "
            + "by the index number used in the last person listing. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + PREFIX_REMARK + "[REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_REMARK + "Likes to swim.";

    public static final String MESSAGE_REMARK_SUCCESS = "Remark edited for Person: ";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MISSING_PERSON = "Person cannot be found";

    private final Index index;
    private final Remark remark;


    public RemarkCommand (Index index, Remark remark) {
        requireNonNull(index);
        requireNonNull(remark);

        this.index = index;
        this.remark = remark;
    }

    public RemarkCommand (Index index, String remark) {
        requireNonNull(index);
        requireNonNull(remark);

        this.index = index;
        this.remark = new Remark(remark);
    }
    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        CommandUtil.checksIndexSmallerThanList(lastShownList, index);

        ReadOnlyPerson personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = generateNewEditedPerson(personToEdit);

        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError(MISSING_PERSON);
        }

        model.updateListToShowAll();
        return new CommandResult(MESSAGE_REMARK_SUCCESS + personToEdit.getName().toString());
    }

    /**
     * Change the remark field of the selected person to edit
     * @param personToEdit Selected person to edit
     * @return A new person with remark field edited
     */
    private Person generateNewEditedPerson(ReadOnlyPerson personToEdit) {
        return new Person(personToEdit.getName(), personToEdit.getPhone(), personToEdit.getEmail(),
                    personToEdit.getAddress(), remark, personToEdit.getBirthday(), personToEdit.getTags(),
                    personToEdit.getPicture(), personToEdit.getFavourite());
    }


    @Override
     public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }
        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        // state check
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index) && remark.equals(e.remark);
    }
}
```
###### \main\java\seedu\address\logic\commands\SortCommand.java
``` java
/**
 * Sorts a the list of persons in ascending alphabetical order
 */
public class SortCommand extends UndoableCommand {
    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_SORT_SUCCESS = "Sorted in ascending order: ";
    public static final String MESSAGE_SORT_FAILURE = "Invalid command format!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sorts the person identified by the index number used in the last person listing.\n"
            + "Parameters: name/num/address/email (the type of sort to be executed)\n"
            + "Example: " + COMMAND_WORD + " name"
            + "Example: " + COMMAND_WORD + " number"
            + "Example: " + COMMAND_WORD + " address"
            + "Example: " + COMMAND_WORD + " remark"
            + "Example: " + COMMAND_WORD + " email"
            + "Example: " + COMMAND_WORD + " birthday";


    private String sortType;

    public SortCommand(String sortType) {
        this.sortType = sortType.toLowerCase();
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        model.sortPerson(sortType);
        return new CommandResult(MESSAGE_SORT_SUCCESS + sortType);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SortCommand // instanceof handles nulls
                && this.sortType.equals(((SortCommand) other).sortType)); // state check
    }
}
```
###### \main\java\seedu\address\logic\commands\UndoCommand.java
``` java
    public UndoCommand(int numUndo) {
        this.numUndo = numUndo;
    }

    public UndoCommand() throws IllegalValueException {
            numUndo = ParserUtil.parseNumber(NUMBER_ONE);
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        checkStackNotEmpty();
        checkUndoSizeNotBiggerThanStack();

        undoMultipleTimes();
        return new CommandResult(MESSAGE_SUCCESS);
    }
    /**
     * Undo for numUndo number of times
     * @throws CommandException if undo while stack is empty
     */
    private void undoMultipleTimes() throws CommandException {
        for (int i = INDEX_ZERO; i < numUndo; i++) {
            if (!undoRedoStack.canUndo()) {
                throw new CommandException(MESSAGE_FAILURE);
            }

            undoRedoStack.popUndo().undo();
        }
    }

    /**
     * Checks if number of undos is not bigger than current avaliable number of undo
     * @throws CommandException if redo while stack is empty
     */
    private void checkUndoSizeNotBiggerThanStack() throws CommandException {
        if (numUndo > undoRedoStack.getUndoStackSize()) {
            throw new CommandException(MESSAGE_TOO_MANY_UNDO + undoRedoStack.getUndoStackSize());
        }
    }

    /**
     * Checks if current number of redo avaliable is zero
     * @throws CommandException if current stack size is zero
     */
    private void checkStackNotEmpty() throws CommandException {
        if (undoRedoStack.getUndoStackSize() == 0) {
            throw new CommandException(MESSAGE_EMPTYSTACK);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UndoCommand // instanceof handles nulls
                && this.numUndo == (((UndoCommand) other).numUndo)); // state check
    }
```
###### \main\java\seedu\address\logic\parser\AddCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final int SIZE_2 = 2;

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
            if (containsAnyPrefix(args)) {
                validatesAllPrefixPresent(argMultimap);
                return createNewPerson(argMultimap);
            } else {
                return alternativeCreateNewPerson(args);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Creates a new person without using any prefex
     * @param args input string given
     * @return New AddCommand with a new person
     * @throws IllegalValueException Invalid parameter for any of person's details
     */
    private AddCommand alternativeCreateNewPerson(String args) throws IllegalValueException {
        String[] allArgs = args.split(",");
        checkNameFormat(allArgs);

        //Initial person's details
        Name name = new Name(allArgs[0]);
        Remark remark = new Remark("");
        Birthday birthday = new Birthday("");
        Email email;
        Phone phone;
        String blocknum;
        String streetnum;
        String unitnum;
        String postalnum = "";
        Favourite favourite = new Favourite("false");
        ProfilePicture picture = new ProfilePicture("default");

        //Generate person's details
        email = getEmailFromString(args);
        blocknum = getBlockFromString(args);
        streetnum = getStreetFromString(args);
        unitnum = getUnitFromString(args);
        postalnum = getPostalFromString(args, postalnum);
        phone = getPhoneFromString(args);
        birthday = getBirthdayFromString(args, birthday);
        Address address = new Address(blocknum + ", " + streetnum + ", " + unitnum + postalnum);
        Set<Tag> tagList = new HashSet<>();
        ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                favourite);
        return new AddCommand(person);
    }

    /**
     * Go through the list of string to look for a valid birthday parameter
     * @param args input string given by user
     * @param birthday Birthday object to store birthday details of a person
     * @return new Birthday if found a valid birthday
     * @throws IllegalValueException Nothing conforms to legal birthday format
     */
    private Birthday getBirthdayFromString(String args, Birthday birthday) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;Pattern birthpattern = Pattern.compile("\\d{1,2}-\\d{1,2}-\\d{4,4}", Pattern.CASE_INSENSITIVE);
        matcher = birthpattern.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            if (Birthday.isValidBirthday(matcher.group(0))) {
                birthday = new Birthday(matcher.group(0));
            } else {
                throw new IllegalValueException("invalid birthday,\n Example: 12-09-1994");
            }
        }
        return birthday;
    }

    /**
     * Go through the list of string to look for a valid phone number parameter
     * @param args input string given by user
     * @return new Phone if found a valid birthday
     * @throws IllegalValueException Nothing conforms to legal birthday format
     */
    private Phone getPhoneFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        Phone phone;Pattern phonepattern = Pattern.compile("\\ {0,1}\\d{8}\\ {0,1}");
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
        return phone;
    }

    /**
     * Go through the list of string to look for a valid postal parameter
     * No exception as this field is optional
     * @param args input string given by user
     * @param postalnum empty postal number
     * @return new Postal if found a valid Postal Number
     */
    private String getPostalFromString(String args, String postalnum) {
        Matcher matcher;
        boolean matchFound;Pattern postal = Pattern.compile("singapore \\d{6,6}", Pattern.CASE_INSENSITIVE);
        matcher = postal.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            postalnum = ", " + matcher.group(0);
        }
        return postalnum;
    }

    /**
     * Go through the list of string to look for a valid unit parameter
     * @param args input string given by user
     * @return new unit if found a valid unit number
     * @throws IllegalValueException Nothing conforms to legal unit format
     */
    private String getUnitFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String unitnum;Pattern unit = Pattern.compile("#\\d\\d-\\d{1,3}[a-zA-Z]{0,1}", Pattern.CASE_INSENSITIVE);
        matcher = unit.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            unitnum = matcher.group(0);
        } else {
            throw new IllegalValueException("invalid address, Unit. \n Example: #01-12B"
                + AddCommand.MESSAGE_USAGE_ALT);
        }
        return unitnum;
    }

    /**
     * Go through the list of string to look for a valid street parameter
     * @param args input string given by user
     * @return new street if found a valid street format
     * @throws IllegalValueException Nothing conforms to legal street format
     */
    private String getStreetFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String streetnum;Pattern street = Pattern.compile("[a-zA-z]+ street \\d{1,2}", Pattern.CASE_INSENSITIVE);
        matcher = street.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            streetnum = matcher.group(0);
        } else {
            throw new IllegalValueException("invalid address, Street. \nExample: Jurong Street 11"
                + AddCommand.MESSAGE_USAGE_ALT);
        }
        return streetnum;
    }

    /**
     * Go through the list of string to look for a valid block parameter
     * @param args input string given by user
     * @return new block if found a valid block format
     * @throws IllegalValueException Nothing conforms to legal block format
     */
    private String getBlockFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        String blocknum;Pattern block = Pattern.compile("block \\d{1,3}", Pattern.CASE_INSENSITIVE);
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
        return blocknum;
    }

    /**
     * Go through the list of string to look for a valid email parameter
     * @param args input string given by user
     * @return new email if found a valid email format
     * @throws IllegalValueException Nothing conforms to legal email format
     */
    private Email getEmailFromString(String args) throws IllegalValueException {
        Matcher matcher;
        boolean matchFound;
        Email email;Pattern emailpattern = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        matcher = emailpattern.matcher(args);
        matchFound = matcher.find();
        if (matchFound) {
            email = new Email(matcher.group(0));
        } else {
            throw new IllegalValueException("invalid email\n Example: Jason@example.com");
        }
        return email;
    }

    /**
     *  Go through the list of string to look for a valid email parameter
     * @param allArgs input string given by user
     * @throws IllegalValueException Nothing conforms to legal email format
     */
    private void checkNameFormat(String[] allArgs) throws IllegalValueException {
        if (allArgs.length < SIZE_2) {
            throw new IllegalValueException("Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
        }
    }

    /**
     * Adds a person using fields formatted by prefixes
     * @param argMultimap All the prefix that should be used.
     * @return A new add command with the new person.
     * @throws IllegalValueException invalid parameter type
     */
    private AddCommand createNewPerson(ArgumentMultimap argMultimap) throws IllegalValueException {
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).get();
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).get();
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).get();
        Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK)).get();
        Birthday birthday = ParserUtil.parseBirthday(argMultimap.getValue(PREFIX_BIRTHDAY)).get();
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Favourite favourite = new Favourite(Favourite.COLOR_OFF);
        ProfilePicture picture = new ProfilePicture("default");
        ReadOnlyPerson person = new Person(name, phone, email, address, remark, birthday, tagList, picture,
                favourite);
        return new AddCommand(person);
    }

    /**
     * Checks if all prefixes are present
     * @param argMultimap All the prefixes to be used
     * @throws ParseException Missing prefix
     */
    private void validatesAllPrefixPresent(ArgumentMultimap argMultimap) throws ParseException {
        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Checks if user input uses any prefix of any type
     * @param args User input
     * @return true if contains prefix, false if does not
     */
    private boolean containsAnyPrefix(String args) {
        return args.contains(PREFIX_NAME.toString()) || args.contains(PREFIX_ADDRESS.toString())
            || args.contains(PREFIX_EMAIL.toString()) || args.contains(PREFIX_PHONE.toString())
            || args.contains(PREFIX_REMARK.toString()) || args.contains(PREFIX_TAG.toString());
    }
```
###### \main\java\seedu\address\logic\parser\AddressBookParser.java
``` java
    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string, adds suggestion to Autocomplete.xml if valid command
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case EmailCommand.COMMAND_WORD:
            Command emailCommand = new EmailCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return emailCommand;

        case SortCommand.COMMAND_WORD:
            return new SortCommandParser().parse(arguments);

        case AddCommand.COMMAND_WORD:
        case AddCommand.COMMAND_ALIAS:
            Command addCommand = new AddCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return addCommand;

        case EditCommand.COMMAND_WORD:
        case EditCommand.COMMAND_ALIAS:
            Command editCommand = new EditCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return editCommand;

        case SelectCommand.COMMAND_WORD:
        case SelectCommand.COMMAND_ALIAS:
            Command selectCommand = new SelectCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return selectCommand;

        case DeleteCommand.COMMAND_WORD:
        case DeleteCommand.COMMAND_ALIAS:
            Command deleteCommand = new DeleteCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return deleteCommand;

        case RemoveTagCommand.COMMAND_WORD:
            Command removeTagCommand = new RemoveTagCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return removeTagCommand;

        case ClearCommand.COMMAND_WORD:
        case ClearCommand.COMMAND_ALIAS:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
        case FindCommand.COMMAND_ALIAS:
            Command findCommand = new FindCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return findCommand;

        case FuzzyfindCommand.COMMAND_WORD:
        case FuzzyfindCommand.COMMAND_ALIAS:
            return new FuzzyfindCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
        case ListCommand.COMMAND_ALIAS:
            return new ListCommand();

        case HistoryCommand.COMMAND_WORD:
        case HistoryCommand.COMMAND_ALIAS:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
        case UndoCommand.COMMAND_ALIAS:
            Command undoCommand = new UndoCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return undoCommand;

        case RedoCommand.COMMAND_WORD:
        case RedoCommand.COMMAND_ALIAS:
            Command redoCommand = new RedoCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return redoCommand;

        case RemarkCommand.COMMAND_WORD:
            Command remarkCommand = new RemarkCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return remarkCommand;

        case BirthdayCommand.COMMAND_WORD:
            Command birthdayCommand = new BirthdayCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return birthdayCommand;

        case MapCommand.COMMAND_WORD:
            Command mapCommand = new MapCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return mapCommand;

        case ImageCommand.COMMAND_WORD:
            return new ImageCommandParser().parse(arguments);

        case FavouriteCommand.COMMAND_WORD:
            Command favouriteCommand = new FavouriteCommandParser().parse(arguments);
            addValidInputToAutocomplete(userInput);
            return favouriteCommand;

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Updates Storage for autocomplete with the valid command
     * @param userInput User's command
     * @throws ParseException storagefile is missing
     */
    private void addValidInputToAutocomplete(String userInput) throws ParseException {
        try {
            CommandBox.setAddSuggestion(userInput);
        } catch (Exception ex) {
            throw new ParseException(MISSING_AUTOCOMPLETEFILE);
        }
    }

}
```
###### \main\java\seedu\address\logic\parser\EmailCommandParser.java
``` java
/**
 * Parses input arguments and creates a new EmailCommand object
 */
public class EmailCommandParser implements Parser<EmailCommand> {

    public static final String EMPTY_STRING = "";

    /**
     * Parses the given {@code String} of arguments in the context of the EmailCommand
     * and returns a Email object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public EmailCommand parse(String args) throws ParseException {
        requireNonNull(args);
        assert args != EMPTY_STRING;
        Index index;
        String subject;
        String message;
        try {
            String[] messages = args.trim().split(",");
            checkValidNumberOfArguments(messages);
            String[] splitArgs = messages[0].trim().split(" ");
            index = ParserUtil.parseIndex(splitArgs[0]);
            subject = (messages[1]);
            message = (messages[2]);
        } catch (IllegalValueException ive) {
            return makeParseException();
        }
        return new EmailCommand(index, subject, message);
    }

    private EmailCommand makeParseException() throws ParseException {
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmailCommand.MESSAGE_USAGE));
    }

    private void checkValidNumberOfArguments(String[] messages) throws ParseException {
        if (messages.length != 3) {
            makeParseException();
        }
    }
}
```
###### \main\java\seedu\address\logic\parser\FavouriteCommandParser.java
``` java
/**
 * Parses arguments and returns FavouriteCommand
 */
public class FavouriteCommandParser implements Parser<FavouriteCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the BirthdayCommand
     * and returns a BirthdayCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public FavouriteCommand parse(String args) throws ParseException {
        requireNonNull(args);

        Index index;
        Favourite favourite;
        try {
            index = ParserUtil.parseIndex(args);
            favourite = new Favourite("true");
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FavouriteCommand.MESSAGE_USAGE), ive);
        }

        return new FavouriteCommand(index, favourite);
    }
}
```
###### \main\java\seedu\address\logic\parser\ParserUtil.java
``` java
    /**
     * Parses {@code number} into an {@code Integer} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified number is invalid (not non-zero unsigned integer).
     */
    public static int parseNumber(String number) throws IllegalValueException {
        String trimmedNumber = number.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedNumber)) {
            throw new IllegalValueException(MESSAGE_INVALID_INDEX);
        }
        return Integer.parseInt(trimmedNumber);
    }

    /**
     * Parses {@code sortType}returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws IllegalValueException if the specified index is invalid (not valid sorting type).
     */
    public static String parseSortType(String sortType) throws IllegalValueException {
        String toSort = sortType.trim().toLowerCase();
        if (!toSort.equals(SORTNAME_ARG) && !toSort.equals(SORTNUM_ARG)
            && !toSort.equals(SORTADD_ARG) && !toSort.equals(SORTEMAIL_ARG)
            && !toSort.equals(SORTREMARK_ARG) && !toSort.equals(SORTBIRTHDAY_ARG)) {
            throw new IllegalValueException(MESSAGE_INVALID_SORT);
        }
        return toSort;
    }
```
###### \main\java\seedu\address\logic\parser\RedoCommandParser.java
``` java
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
```
###### \main\java\seedu\address\logic\parser\RemarkCommandParser.java
``` java
/**
 * Parses input arguments and creates a new AddCommand object
 */
public class RemarkCommandParser implements Parser<RemarkCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the RemarkCommand
     * and returns a RemarkCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public RemarkCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_REMARK);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
        }
        Remark remark = new Remark(argMultimap.getValue(PREFIX_REMARK).orElse(""));
        return new RemarkCommand(index, remark);
    }
}
```
###### \main\java\seedu\address\logic\parser\SortCommandParser.java
``` java
/**
 * Parses input arguments and creates a new SortCommand object
 */
public class SortCommandParser implements Parser<SortCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SortCommand
     * and returns an SortCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SortCommand parse(String args) throws ParseException {
        try {
            args = args.toLowerCase();
            String toSort = ParserUtil.parseSortType(args);
            return new SortCommand(toSort);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SortCommand.MESSAGE_USAGE));
        }
    }
}
```
###### \main\java\seedu\address\logic\parser\UndoCommandParser.java
``` java
/**
 * Parses input arguments and creates a new UndoCommand object
 */
public class UndoCommandParser implements Parser<UndoCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the UndoCommand
     * and returns an UndoCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public UndoCommand parse(String args) throws ParseException {

        String[] splitArgs = args.trim().split(" ");

        int numUndo;
        try {
            if (splitArgs[0].trim().equals("")) {
                numUndo = 1;
            } else {
                numUndo = ParserUtil.parseNumber(splitArgs[0]);
            }
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE), ive);
        }

        return new UndoCommand(numUndo);
    }

}
```
###### \main\java\seedu\address\logic\UndoRedoStack.java
``` java
    /**
     * Gets number of undoable tasks in integer
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Gets number of redoable tasks in integer
     */
    public int getRedoStackSize() {
        return redoStack.size();
    }

```
###### \main\java\seedu\address\model\AddressBook.java
``` java
    /**
     *     Sort Persons according to sortType
     */
    public void sortPersons(String sortType) {
        persons.sort(sortType);
        syncMasterTagListWith(persons);
    }
```
###### \main\java\seedu\address\model\Model.java
``` java
    /**Sorts all the people in the current database*/
    void sortPerson(String sortType);
```
###### \main\java\seedu\address\model\ModelManager.java
``` java
    @Override
    public void updateListToShowAll() {
        filteredPersons.setPredicate(null);
    }
```
###### \main\java\seedu\address\model\person\Favourite.java
``` java
/**
 * Represents a person's Favourite/color on in the addressBook
 * Guarantees: immutable; is valid as declared in {@link #isValidInput(String)}
 */
public class Favourite {

    public static final String MESSAGE_FAVOURITE_CONSTRAINTS =
            "Person favourite should only be true or false, and it should not be blank";
    public static final String COLOR_SWITCH = "true";
    public static final String COLOR_OFF = "false";
    private Boolean colorOn = false;

    /**
     * Validates given Favourite.
     *
     * @throws IllegalValueException if given favourite string is invalid.
     */
    public Favourite(String input) throws IllegalValueException {
        input = processNoInput(input);
        String trimmedinput = input.trim();
        isValidInput(trimmedinput);
        //Confirms the input is legal, COLOR_SWITCH or COLOR_OFF
        assert input.equals(COLOR_SWITCH)||input.equals(COLOR_OFF);
        updateColor(trimmedinput);
    }

//Returns false if no input
    private String processNoInput(String input) {
        if (input == null) {
            input = COLOR_OFF;
        }
        return input;
    }

//Changes the colorOn state if input is true
    private void updateColor(String trimmedinput) {
        if (trimmedinput.equals(COLOR_SWITCH) && !colorOn) {
            this.colorOn = true;
        } else {
            this.colorOn = false;
        }
    }

    /**
     * Inverses the current state of Favourite
     */
    public void inverse() {
        colorOn = false;
    }

    /**
     * Returns true if a given string is a favourite type
     * Throws Exception if invalid input.
     */
    public void isValidInput(String input) throws IllegalValueException {
        if (input.toLowerCase().equals(COLOR_SWITCH) || input.toLowerCase().equals(COLOR_OFF)) {
            return;
        } else {
            throw new IllegalValueException(MESSAGE_FAVOURITE_CONSTRAINTS);
        }
    }

    @Override
    public String toString() {
        if (colorOn) {
            return COLOR_SWITCH;
        } else {
            return COLOR_OFF;
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Favourite // instanceof handles nulls
                && this.colorOn.equals(((Favourite) other).colorOn)); // state check
    }
}
```
###### \main\java\seedu\address\model\person\UniquePersonList.java
``` java
    /**
     * Sorts the internalList as declared by the arguments
     */
    public void sort(String sortType) {
        if (sortType.equals(SORTNAME_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getName().toString().compareTo(p2.getName().toString()));
        } else if (sortType.equals(SORTNUM_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getPhone().toString().compareTo(p2.getPhone().toString()));
        } else if (sortType.equals(SORTADD_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getAddress().toString().compareTo(p2.getAddress().toString()));
        } else if (sortType.equals(SORTEMAIL_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getEmail().toString().compareTo(p2.getEmail().toString()));
        } else if (sortType.equals(SORTREMARK_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getRemark().toString().compareTo(p2.getRemark().toString()));
        } else if (sortType.equals(SORTBIRTHDAY_ARG)) {
            Collections.sort(internalList, (Person p1, Person p2) ->
                p1.getBirthday().toString().compareTo(p2.getBirthday().toString()));
        }
    }
```
###### \main\java\seedu\address\Sound.java
``` java
/**
 * Plays a beep Sound.
 */
public class Sound {
    private static final Logger logger = LogsCenter.getLogger("Error Sound");
    public static final String EMPTY = "";
    private static ArrayList<String> musicList = new ArrayList<String>(Arrays.asList("ErrorSound.mp3"));
    private static int curr = 0;
    private static String bip;
    private static Media hit;
    private static MediaPlayer mediaPlayer;

    /**
     * start playing the first error invalidCommandSound on the playlist.
     */
    public static void invalidCommandSound() {
        try {
            createsNewMediaPlayer();
            mediaPlayer.play();
        } catch (Exception ex) {
            logger.info("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    private static void createsNewMediaPlayer() throws URISyntaxException {
        bip = musicList.get(curr);
        //must be a valid file name before begin searching
        assert bip != EMPTY;
        hit = new Media(Thread.currentThread().getContextClassLoader().getResource(bip).toURI().toString());
        mediaPlayer = new MediaPlayer(hit);
    }
}
```
###### \main\java\seedu\address\ui\CommandBox.java
``` java
/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String AUTOCOMPLETE_FILE_NAME = "Autocomplete.xml";
    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    public static final String STORAGE_FILE_NAME = "Autocomplete.xml";
    public static final String ERROR_MESSAGE_CREATE_FILE_FAILED = "Unable to create file Autocomplete.xml";
    private static String[] possibleSuggestion = {"add", "birthday", "clear", "list", "help", "removetag", "image",
        "edit", "find", "delete", "select", "favourite", "history", "undo", "redo", "email", "sort", "sort name", "map",
        "sort number", "sort email", "sort address", "sort remark", "sort birthday", "exit"};
    private static ArrayList<String> mainPossibleSuggestion = new ArrayList<String>(Arrays.asList(possibleSuggestion));
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private final Logic logic;
    private ListElementPointer historySnapshot;

    @FXML
    private TextField commandTextField;
    private ArrayList<String> prevText = new ArrayList<String>();
    private AutoCompletionBinding autocompletionbinding;
    public CommandBox(Logic logic) {
        super(FXML);
        this.logic = logic;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        historySnapshot = logic.getHistorySnapshot();
        updateAutocomplete();
        autocompletionbinding = TextFields.bindAutoCompletion(commandTextField, mainPossibleSuggestion);
    }

    /**
     * Updates autocomplete with Autocomplete.xml File
     */
    private void updateAutocomplete() {
        try {
            updateAutocompleteWithStorageFile();
        } catch (Exception ex) {
            createNewStorageFile(ex);
        }
    }

    private void updateAutocompleteWithStorageFile() throws FileNotFoundException {
        XMLDecoder e = readStorageFile();
        mainPossibleSuggestion = ((ArrayList<String>) e.readObject());
        e.close();
    }

    private void createNewStorageFile(Exception ex)  {
        try {
            File file = new File(STORAGE_FILE_NAME);
            file.createNewFile();
            addNewDataInStorage("");
        } catch (IOException ioe) {
            raise(new DataSavingExceptionEvent(ex));
        }
    }

    private XMLDecoder readStorageFile() throws FileNotFoundException {
        return new XMLDecoder(new FileInputStream(STORAGE_FILE_NAME));
    }

```
###### \main\java\seedu\address\ui\CommandBox.java
``` java

    /**
     * Adds in a valid command string into autocomplete.xml storage
     * @param commandWord
     * @throws CommandException if autocomplete.xml cannot be made.
     */
    public static void setAddSuggestion(String commandWord) throws CommandException {
        if (mainPossibleSuggestion.contains(commandWord)) {
            return;
        }
        assert mainPossibleSuggestion.contains(commandWord);
        try {
            addNewDataInStorage(commandWord);
        } catch (Exception ex) {
            createNewStorageFile();
        }

    }

    private static void createNewStorageFile() throws CommandException {
        try {
            File file = new File(STORAGE_FILE_NAME);
            file.createNewFile();
            addNewDataInStorage("");
        } catch (IOException ioe) {
            throw new CommandException(ERROR_MESSAGE_CREATE_FILE_FAILED);
        }
    }

    //Updates Storagefile with new content, used as refresh if no content
    private static void addNewDataInStorage(String commandWord) throws FileNotFoundException {
        if(!commandWord.equals("")) {
            mainPossibleSuggestion.add(commandWord.trim());
        }
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(AUTOCOMPLETE_FILE_NAME)));
        e.writeObject(mainPossibleSuggestion);
        e.close();
    }

```
###### \main\java\seedu\address\ui\PersonCard.java
``` java
    /**
     * Binds the individual UI elements to observe their respective {@code Person} properties
     * so that they will be notified of any changes.
     * Changes the color code of the person if favourite.
     */
    private void bindListeners(ReadOnlyPerson person) {
        name.textProperty().bind(Bindings.convert(person.nameProperty()));
        if (person.getFavourite().toString().equals("true")) {
            name.setStyle("-fx-background-color : #ff0000");
        } else if (person.getFavourite().toString().equals("false")) {
            name.setStyle("-fx-background-color : transparent");
        }
    }
```
###### \test\java\seedu\address\logic\commands\RedoCommandTest.java
``` java
    @Test
    public void alternative() throws Exception {
        UndoRedoStack undoRedoStack = prepareStack(
                Collections.emptyList(), Arrays.asList(deleteCommandOne, deleteCommandOne));
        RedoCommand redoCommand = new RedoCommand(2);
        redoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

        // multiple commands in redoStack
        deleteFirstPerson(expectedModel);
        deleteFirstPerson(expectedModel);
        assertCommandSuccess(redoCommand, model, RedoCommand.MESSAGE_SUCCESS, expectedModel);

        // no command in redoStack
        assertCommandFailure(redoCommand, model, RedoCommand.MESSAGE_FAILURE);
    }
}
```
###### \test\java\seedu\address\logic\commands\RemarkCommandTest.java
``` java
public class RemarkCommandTest {

    @Test
    public void equals() {
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY);
        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_AMY);
        assertTrue(standardCommand.equals(commandWithSameValues));
        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));
        // null -> returns false
        assertFalse(standardCommand.equals(null));
        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));
        // different index -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, VALID_REMARK_AMY)));
        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, VALID_REMARK_BOB)));
    }
}
```
###### \test\java\seedu\address\logic\commands\SortCommandTest.java
``` java
/**
 * Contains integration tests (interaction with the Model) for {@code SortCommand}.
 */
public class SortCommandTest {

    public static final String SORT_NAME_ARG = "name";
    public static final String SORT_EMAIL_ARG = "email";
    public static final String SORT_ADDRESS_ARG = "address";
    public static final String SORT_NUM_ARG = "number";
    public static final String SORT_NUM_ARG_CAMELCASE = "NuMBeR";
    private Model model;
    private Model expectedModel;
    private SortCommand sortCommand;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }
    @Test
    public void execute_showsSameList() {
        //Sort name -> command parsed successful
        sortCommand = new SortCommand(SORT_NAME_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NAME_ARG, expectedModel);

        //Sort email -> command parsed successful
        expectedModel = new ModelManager(getSortedEmailAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_EMAIL_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_EMAIL_ARG, expectedModel);

        //Sort address -> command parsed successful
        expectedModel = new ModelManager(getSortedAddressAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_ADDRESS_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_ADDRESS_ARG, expectedModel);

        //Sort number -> command parsed successful
        expectedModel = new ModelManager(getSortedNumAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_NUM_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NUM_ARG, expectedModel);

        //Sort number CamelCase -> command parsed successful
        expectedModel = new ModelManager(getSortedNumAddressBook(), new UserPrefs());
        sortCommand = new SortCommand(SORT_NUM_ARG);
        sortCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        assertCommandSuccess(sortCommand, model, SortCommand.MESSAGE_SORT_SUCCESS + SORT_NUM_ARG, expectedModel);

    }
}

```
###### \test\java\seedu\address\logic\commands\UndoCommandTest.java
``` java
    @Test
    public void alternative() throws Exception {
        UndoRedoStack undoRedoStack = prepareStack(
                Arrays.asList(deleteCommandOne, deleteCommandTwo), Collections.emptyList());
        UndoCommand undoCommand = new UndoCommand(2);
        undoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        deleteCommandOne.execute();
        deleteCommandTwo.execute();

        // multiple commands in undoStack
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());;
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // single command in undoStack
        undoRedoStack = prepareStack(
                Arrays.asList(deleteCommandOne), Collections.emptyList());
        undoCommand = new UndoCommand();
        undoCommand.setData(model, EMPTY_COMMAND_HISTORY, undoRedoStack);
        deleteCommandOne.execute();
        expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        assertCommandSuccess(undoCommand, model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
```
###### \test\java\seedu\address\logic\parser\AddCommandParserTest.java
``` java
    @Test
    public void parse_allFieldsPresentAlternative_success() {
        Person expectedPerson = new PersonBuilder().withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags().withBirthday("11-11-2010").build();
        //Valid input format - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple phone, 1st Phone accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 33333333 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Email, 1st Email accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com  pop@example.com" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Addresses, 1st Address accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com  Block 122 Poppy Street 88 #11-111" + " 11-11-2010", new AddCommand(expectedPerson));

        //Multiple Birthdays, 1st Birthday accepted - Accepted
        assertParseSuccess(parser, "Bob Choo, 22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com " + " 11-11-2010" + " 12-12-2012", new AddCommand(expectedPerson));

    }
```
###### \test\java\seedu\address\logic\parser\AddCommandParserTest.java
``` java
    @Test
    public void parse_altcompulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name field
        assertParseFailure(parser, "22222222 Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "Missing Name!\n" + AddCommand.MESSAGE_USAGE_ALT);
        // missing phone field
        assertParseFailure(parser, "JohnDoe, Block 123 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "Number should be 8 digits long!\n"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Block field
        assertParseFailure(parser, "JohnDoe, 11111111 Bobby Street 3 #01-123 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Block Number. \nExample: Block 123"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Street field
        assertParseFailure(parser, "JohnDoe, 11111111 Block 123 #01-123 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Street. \nExample: Jurong Street 11"
                + AddCommand.MESSAGE_USAGE_ALT);
        // missing Address/Unit field
        assertParseFailure(parser, "JohnDoe, 11111111 Block 123 Bobby Street 3 "
                + "bob@example.com" + " 11-11-2010", "invalid address, Unit. \n Example: #01-12B"
                + AddCommand.MESSAGE_USAGE_ALT);
    }
```
###### \test\java\seedu\address\logic\parser\EmailCommandParserTest.java
``` java
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
```
###### \test\java\seedu\address\logic\parser\FavouriteCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the FavouriteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class FavouriteCommandParserTest {

    public static final String FIRST_PERSON = "1";
    private FavouriteCommandParser parser = new FavouriteCommandParser();

    @Test
    public void parse_validArgs_returnsFavouriteCommand() throws IllegalValueException {
            assertParseSuccess(parser, FIRST_PERSON, new FavouriteCommand(INDEX_FIRST_PERSON,
                    new Favourite(FIRST_PERSON)));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                FavouriteCommand.MESSAGE_USAGE));
    }
}
```
###### \test\java\seedu\address\logic\parser\RedoCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the MapCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the MapCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class RedoCommandParserTest {

    private RedoCommandParser parser = new RedoCommandParser();

    @Test
    public void parse_validArgs_returnsRedoCommand() {
        assertParseSuccess(parser, "1", new RedoCommand(1));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RedoCommand.MESSAGE_USAGE));
    }
}
```
###### \test\java\seedu\address\logic\parser\RemarkCommandParserTest.java
``` java
public class RemarkCommandParserTest {
    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_indexSpecified_failure() throws Exception {
        final String remark = "Some remark";

        //Have remarks
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + " " + remark;
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, remark);
        assertParseSuccess(parser, userInput, expectedCommand);

        //No remarks
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, "");
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_noFieldSpecified_failure() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // nothing at all
        assertParseFailure(parser, RemarkCommand.COMMAND_WORD, expectedMessage);
    }

}
```
###### \test\java\seedu\address\logic\parser\SortCommandParserTest.java
``` java
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
```
###### \test\java\seedu\address\logic\parser\UndoCommandParserTest.java
``` java
/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the MapCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the MapCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class UndoCommandParserTest {

    private UndoCommandParser parser = new UndoCommandParser();

    @Test
    public void parse_validArgs_returnsUndoCommand() {
        assertParseSuccess(parser, "1", new UndoCommand(1));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, UndoCommand.MESSAGE_USAGE));
    }
}
```
###### \test\java\systemtests\SortCommandSystemTest.java
``` java
public class SortCommandSystemTest extends AddressBookSystemTest {
    @Test
    public void sort() throws Exception {
        Model model = getModel();

        /* Case: Sort all persons by name, CamelCase */
        String command = SortCommand.COMMAND_WORD + " nAmE";
        String expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "name";
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by null*/
        command = SortCommand.COMMAND_WORD + "  ";
        expectedResultMessage = SortCommand.MESSAGE_SORT_FAILURE + " \n" + SortCommand.MESSAGE_USAGE;
        assertCommandFailure(command, expectedResultMessage);

        /* Case: Sort all persons by name */
        command = SortCommand.COMMAND_WORD + " name";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "name";
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by address */
        command = SortCommand.COMMAND_WORD + " address";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "address";
        model.sortPerson("address");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by number */
        command = SortCommand.COMMAND_WORD + " number";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "number";
        model.sortPerson("number");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by remark */
        command = SortCommand.COMMAND_WORD + " remark";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "remark";
        model.sortPerson("remark");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by birthday */
        command = SortCommand.COMMAND_WORD + " birthday";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "birthday";
        model.sortPerson("birthday");
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: Sort all persons by invalid argument */
        command = SortCommand.COMMAND_WORD + " xxx";
        expectedResultMessage = SortCommand.MESSAGE_SORT_FAILURE + " \n" + SortCommand.MESSAGE_USAGE;
        assertCommandFailure(command, expectedResultMessage);

        /* Adds a person AMY to the addressBook */
        ReadOnlyPerson toAdd = AMY;
        command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_AMY + "  " + PHONE_DESC_AMY + " "
                + EMAIL_DESC_AMY + "   " + ADDRESS_DESC_AMY + "   " + TAG_DESC_FRIEND + " ";
        assertCommandSuccess(command, toAdd);
        model.addPerson(toAdd);

        /* Case: Sort all persons by number */
        command = SortCommand.COMMAND_WORD + " number";
        expectedResultMessage = SortCommand.MESSAGE_SORT_SUCCESS + "number";
        model.sortPerson("number");
        assertCommandSuccess(command, model, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, ReadOnlyPerson)} except that the result
     * display box displays {@code expectedResultMessage} and the model related components equal to
     * {@code expectedModel}.
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
    }
    /**
     * Performs the same verification as {@code assertCommandSuccess(ReadOnlyPerson)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(ReadOnlyPerson)
     */
    private void assertCommandSuccess(String command, ReadOnlyPerson toAdd) {
        Model expectedModel = getModel();
        try {
            expectedModel.addPerson(toAdd);
        } catch (DuplicatePersonException dpe) {
            throw new IllegalArgumentException("toAdd already exists in the model.");
        }
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }
    /**
     * Executes {@code command} and verifies that the command box displays {@code command}, the result display
     * box displays {@code expectedResultMessage} and the model related components equal to the current model.
     * These verifications are done by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * Also verifies that the browser url, selected card and status bar remain unchanged, and the command box has the
     * error style.
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();
        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }

}
```
