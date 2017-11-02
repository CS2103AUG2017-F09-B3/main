package seedu.address.logic.commands;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsSubstringsPredicate;
import seedu.address.model.person.ReadOnlyPerson;

//@@author bokwoon95
public class FuzzyfindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        NameContainsSubstringsPredicate firstPredicate =
                new NameContainsSubstringsPredicate(Collections.singletonList("first"));
        NameContainsSubstringsPredicate secondPredicate =
                new NameContainsSubstringsPredicate(Collections.singletonList("second"));

        FuzzyfindCommand fuzzyfindFirstCommand = new FuzzyfindCommand(firstPredicate);
        FuzzyfindCommand fuzzyfindSecondCommand = new FuzzyfindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(fuzzyfindFirstCommand.equals(fuzzyfindFirstCommand));

        // same values -> returns true
        FuzzyfindCommand findFirstCommandCopy = new FuzzyfindCommand(firstPredicate);
        assertTrue(fuzzyfindFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(1));

        // null -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(fuzzyfindFirstCommand.equals(fuzzyfindSecondCommand));
    }

    @Test
    //Test basic Find Functionality in Fuzzyfind
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 0);
        FuzzyfindCommand command = prepareCommand(" ");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    //Test basic Find Functionality in Fuzzyfind
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        FuzzyfindCommand command = prepareCommand("Kurz Elle Kunz");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(CARL, ELLE, FIONA));
    }

    @Test
    //Test Fuzzy Find Functionality in Fuzzyfind
    public void execute_fuzzyFindv1() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 5);
        FuzzyfindCommand command = prepareCommand("e");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(ALICE, BENSON, DANIEL, ELLE, GEORGE));
    }

    @Test
    //Test Fuzzy Find Functionality in Fuzzyfind
    public void execute_fuzzyFindv2() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 1);
        FuzzyfindCommand command = prepareCommand("eNsO");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(BENSON));
    }

    /**
     * Parses {@code userInput} into a {@code FindCommand}.
     */
    private FuzzyfindCommand prepareCommand(String userInput) {

        //this is work that the parser will do
        String[] nameKeywords = userInput.split("\\s+");
        NameContainsSubstringsPredicate searchPredicate;
        searchPredicate = new NameContainsSubstringsPredicate(Arrays.asList(nameKeywords));

        FuzzyfindCommand command = new FuzzyfindCommand(searchPredicate);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Asserts that {@code command} is successfully executed, and<br>
     *     - the command feedback is equal to {@code expectedMessage}<br>
     *     - the {@code FilteredList<ReadOnlyPerson>} is equal to {@code expectedList}<br>
     *     - the {@code AddressBook} in model remains the same after executing the {@code command}
     */
    private void assertCommandSuccess(FuzzyfindCommand command, String expectedMessage,
                                      List<ReadOnlyPerson> expectedList) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute();

        assertEquals(expectedMessage, commandResult.feedbackToUser);
        assertEquals(expectedList, model.getFilteredPersonList());
        assertEquals(expectedAddressBook, model.getAddressBook());
    }

}
