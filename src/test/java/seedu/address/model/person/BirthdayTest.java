package seedu.address.model.person;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

//@@author liliwei25
/**
 * Test Birthday class
 */
public class BirthdayTest {

    @Test
    public void isValidBirthday() {
        // invalid birthday
        assertFalse(Birthday.isValidBirthday("1")); // incorrect format
        assertFalse(Birthday.isValidBirthday("50-03-1995")); // incorrect day

        // valid birthday
        assertTrue(Birthday.isValidBirthday("01-01-2001"));
        assertTrue(Birthday.isValidBirthday("06-06-2006"));
        assertTrue(Birthday.isValidBirthday("12-12-2012"));
    }
}
