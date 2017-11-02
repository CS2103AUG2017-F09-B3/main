package seedu.address.model.person;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author thehelpfulbees
/**
 * Counts number of times a person has been searched for
 * Guarantees: immutable;
 */
public class NumTimesSearched {

    public static final String MESSAGE_NUM_TIMES_SEARCHED_CONSTRAINTS =
            "Initial value of NumTimesSearched should be >= 0";

    public static int STARTING_VALUE = 0;

    public int value = STARTING_VALUE; //num times searched

    /**
     * Validates given Favourite.
     *
     * @throws IllegalValueException if given favourite string is invalid.
     */
    public NumTimesSearched(int initialValue) throws IllegalValueException {
        if (!isValidValue(initialValue)) {
            throw new IllegalValueException(MESSAGE_NUM_TIMES_SEARCHED_CONSTRAINTS);
        }
        this.value = initialValue;
    }

    public NumTimesSearched() {
        this.value = STARTING_VALUE;
    }

    public void incrementValue() {
        value++;
    }

    /**
     * Returns true if a given string is a valid person name.
     */
    public static boolean isValidValue(int value) {
        return (value >= 0);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof NumTimesSearched // instanceof handles nulls
                && this.value == ((NumTimesSearched) other).value); // state check
    }
}
