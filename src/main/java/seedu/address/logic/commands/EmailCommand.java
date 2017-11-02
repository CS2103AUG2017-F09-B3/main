package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.ReadOnlyPerson;

//@@author justintkj
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
    private void sendsMessage(String host, String user, String pass, Session mailSession,
                              Message msg) throws MessagingException {
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
    private Message composeMessage(String to, String from, String subject, String messageText,
                                   Session mailSession) throws MessagingException {
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
