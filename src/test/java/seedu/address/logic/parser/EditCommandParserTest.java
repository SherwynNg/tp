package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BMW;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_ENDDATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_STARTDATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BMW;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_ANALYST;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_ENGINEER;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_ADIDAS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BMW;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_ANALYST;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_ENGINEER;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_COMPANY;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.model.company.Date;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_ADIDAS, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_ADIDAS, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_ADIDAS, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag
        assertParseFailure(parser, "1" + INVALID_STARTDATE_DESC, Date.MESSAGE_CONSTRAINTS); // invalid tag
        assertParseFailure(parser, "1" + INVALID_ENDDATE_DESC, Date.MESSAGE_CONSTRAINTS); // invalid tag
        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_ADIDAS, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_ANALYST + TAG_DESC_ENGINEER + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_ANALYST + TAG_EMPTY + TAG_DESC_ENGINEER, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_ANALYST + TAG_DESC_ENGINEER, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_PHONE_ADIDAS,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_COMPANY;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BMW + TAG_DESC_ENGINEER
                + EMAIL_DESC_ADIDAS + NAME_DESC_ADIDAS + TAG_DESC_ANALYST;

        EditCommand.EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_ADIDAS)
                .withPhone(VALID_PHONE_BMW).withEmail(VALID_EMAIL_ADIDAS)
                .withTags(VALID_TAG_ENGINEER, VALID_TAG_ANALYST).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BMW + EMAIL_DESC_ADIDAS;

        EditCommand.EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BMW)
                .withEmail(VALID_EMAIL_ADIDAS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + NAME_DESC_ADIDAS;
        EditCommand.EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(
        ).withName(VALID_NAME_ADIDAS).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_ADIDAS;
        descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_ADIDAS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_ADIDAS;
        descriptor = new EditCompanyDescriptorBuilder().withEmail(VALID_EMAIL_ADIDAS).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);


        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_ANALYST;
        System.out.println(userInput);
        descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_ANALYST).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_COMPANY;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BMW;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BMW + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_ADIDAS + EMAIL_DESC_ADIDAS
                + TAG_DESC_ANALYST + PHONE_DESC_ADIDAS + EMAIL_DESC_ADIDAS + TAG_DESC_ANALYST
                + PHONE_DESC_BMW + EMAIL_DESC_BMW + TAG_DESC_ENGINEER;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL));
    }

    @Test
    public void parse_resetTags_failure() {
        Index targetIndex = INDEX_THIRD_COMPANY;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();

        String expectedOutput = Tag.MESSAGE_CONSTRAINTS;

        assertParseFailure(parser, userInput, expectedOutput);
    }
}
