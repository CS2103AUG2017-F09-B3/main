package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.exceptions.IllegalValueException;

//@@author liliwei25
/**
 * Represents a profile picture for Person
 */
public class ProfilePicture {

    public final String imageLocation;

    /**
     * Validates given name.
     *
     * @throws IllegalValueException if given name string is invalid.
     */
    public ProfilePicture(String location) {
        requireNonNull(location);
        imageLocation = location;
    }

    public String getLocation() {
        return imageLocation;
    }
    @Override
    public String toString() {
        return imageLocation;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ProfilePicture // instanceof handles nulls
                && this.imageLocation.equals(((ProfilePicture) other).imageLocation)); // state check
    }

    @Override
    public int hashCode() {
        return imageLocation.hashCode();
    }
}
