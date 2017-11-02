package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author liliwei25
/**
 * Represents a Person's birthday in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidBirthday(String)}
 */
public class Birthday {

    public static final String MESSAGE_BIRTHDAY_CONSTRAINTS =
            "Birthdays can only contain numbers, and should be in the format dd-mm--yyyy";
    public static final String BIRTHDAY_VALIDATION_REGEX = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1"
            + "|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\"
            + "/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468]"
            + "[048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\"
            + "4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    public final String value;
    public final int day; //Not implemented yet
    public final int month;
    public final int year;

    /**
     * Validates given birthday.
     *
     * @throws IllegalValueException if given birthday string is invalid.
     */
    public Birthday(String birthday) throws IllegalValueException {
        requireNonNull(birthday);
        String trimmedBirthday = birthday.trim();
        if (birthday.equals("") || birthday.equals("Not Set") || birthday.equals("remove")) {
            this.value = "Not Set";
            day = month = year = 0;
        } else if (!isValidBirthday(trimmedBirthday) || !isDateCorrect(trimmedBirthday.split("-"))) {
            throw new IllegalValueException(MESSAGE_BIRTHDAY_CONSTRAINTS);
        } else {
            this.value = trimmedBirthday;
            String[] splitBirthday = value.split("-");
            this.day = Integer.parseInt(splitBirthday[0]);
            this.month = Integer.parseInt(splitBirthday[1]);
            this.year = Integer.parseInt(splitBirthday[2]);
        }

    }

    /**
     * Determines if date entered by user is correct and ensures that it is not after current date
     * @param birthday
     * @return
     */
    public static boolean isDateCorrect(String[] birthday) {
        LocalDateTime now = LocalDateTime.now();
        int day = now.getDayOfMonth();
        int month = now.getMonthValue();
        int year = now.getYear();

        int inputDay = Integer.parseInt(birthday[0]);
        int inputMonth = Integer.parseInt(birthday[1]);
        int inputYear = Integer.parseInt(birthday[2]);

        if (inputYear > year) {
            return false;
        } else if (inputYear == year && inputMonth > month) {
            return false;
        } else if (inputYear == year && inputMonth == month && inputDay > day) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Returns true if a given string is a valid person birthday.
     */
    public static boolean isValidBirthday(String test) {
        return test.matches(BIRTHDAY_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Birthday // instanceof handles nulls
                && this.value.equals(((Birthday) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

}
