package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ParserUtil;
import seedu.address.model.Model;

/**
 * Redo the previously undone command.
 */
public class RedoCommand extends Command {

    public static final String COMMAND_WORD = "redo";
    public static final String COMMAND_ALIAS = "r";

    public static final String MESSAGE_SUCCESS = "Redo success!";
    public static final String MESSAGE_FAILURE = "No more commands to redo!";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Redo Commands identified by the index number used. If no index number, assumed to be 1.\n"
            + "Parameters: (POSITIVE INDEX)\n"
            + "Example: " + COMMAND_WORD + ""
            + "Example: " + COMMAND_WORD + " 1";

    private int numRedo;
    //@@author justintkj
    public RedoCommand(int numRedo) {
        this.numRedo = numRedo;
    }
    public RedoCommand() {
        try {
            numRedo = ParserUtil.parseNumber("1");
        } catch (IllegalValueException ex) {
            System.out.println("Shouldn't reach here");
        }
    }
    @Override
    public CommandResult execute() throws CommandException {
        requireAllNonNull(model, undoRedoStack);

        if (undoRedoStack.getRedoStackSize() == 0) {
            throw new CommandException("No more commands to redo!");
        }
        if (numRedo > undoRedoStack.getRedoStackSize()) {
            throw new CommandException("Maximum redo size: " + undoRedoStack.getRedoStackSize());
        }

        for (int i = 0; i < numRedo; i++) {
            if (!undoRedoStack.canRedo()) {
                throw new CommandException(MESSAGE_FAILURE);
            }

            undoRedoStack.popRedo().redo();
        }

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RedoCommand // instanceof handles nulls
                && this.numRedo == (((RedoCommand) other).numRedo)); // state check
    }
    //@@author
    @Override
    public void setData(Model model, CommandHistory commandHistory, UndoRedoStack undoRedoStack) {
        this.model = model;
        this.undoRedoStack = undoRedoStack;
    }
}
