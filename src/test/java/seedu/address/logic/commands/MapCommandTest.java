package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ReadOnlyPerson;

//@@author liliwei25
/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code MapCommand}.
 */
public class MapCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyPerson personToMap = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        MapCommand mapCommand = prepareCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(MapCommand.MESSAGE_MAP_SHOWN_SUCCESS, personToMap);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.mapPerson(personToMap);

        assertCommandSuccess(mapCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        MapCommand mapCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(mapCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstPersonOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        MapCommand mapCommand = prepareCommand(outOfBoundIndex);

        assertCommandFailure(mapCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        MapCommand mapFirstCommand = new MapCommand(INDEX_FIRST_PERSON);
        MapCommand mapSecondCommand = new MapCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(mapFirstCommand.equals(mapFirstCommand));

        // same values -> returns true
        MapCommand mapFirstCommandCopy = new MapCommand(INDEX_FIRST_PERSON);
        assertTrue(mapFirstCommand.equals(mapFirstCommandCopy));

        // different types -> returns false
        assertFalse(mapFirstCommand.equals(1));

        // null -> returns false
        assertFalse(mapFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(mapFirstCommand.equals(mapSecondCommand));
    }

    /**
     * Returns a {@code MapCommand} with the parameter {@code index}.
     */
    private MapCommand prepareCommand(Index index) {
        MapCommand mapCommand = new MapCommand(index);
        mapCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return mapCommand;
    }
}
