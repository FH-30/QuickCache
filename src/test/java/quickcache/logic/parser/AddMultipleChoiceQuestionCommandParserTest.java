package quickcache.logic.parser;

import static quickcache.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static quickcache.commons.core.Messages.MESSAGE_TOO_MANY_QUESTIONS;
import static quickcache.logic.commands.CommandTestUtil.ANSWER_DESC_ONE;
import static quickcache.logic.commands.CommandTestUtil.ANSWER_DESC_TWO;
import static quickcache.logic.commands.CommandTestUtil.CHOICE_DESC;
import static quickcache.logic.commands.CommandTestUtil.INVALID_ANSWER_DESC;
import static quickcache.logic.commands.CommandTestUtil.INVALID_QUESTION_DESC;
import static quickcache.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static quickcache.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static quickcache.logic.commands.CommandTestUtil.QUESTION_DESC_ONE;
import static quickcache.logic.commands.CommandTestUtil.QUESTION_DESC_THREE;
import static quickcache.logic.commands.CommandTestUtil.QUESTION_DESC_TWO;
import static quickcache.logic.commands.CommandTestUtil.VALID_ANSWER_ONE;
import static quickcache.logic.commands.CommandTestUtil.VALID_QUESTION_ONE;

import org.junit.jupiter.api.Test;

import quickcache.logic.commands.AddMultipleChoiceQuestionCommand;
import quickcache.model.flashcard.Answer;
import quickcache.model.flashcard.Flashcard;
import quickcache.model.flashcard.Question;
import quickcache.testutil.FlashcardBuilder;

public class AddMultipleChoiceQuestionCommandParserTest {
    private final AddMultipleChoiceQuestionCommandParser parser = new AddMultipleChoiceQuestionCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String[] choices = {"First", "Second", "Third", "Fourth"};
        Flashcard expectedFlashcard = new FlashcardBuilder().withMultipleChoiceQuestion("Question One", choices)
            .withAnswer("First").withTags().build();

        // whitespace only preamble
        CommandParserTestUtil.assertParseSuccess(parser,
            PREAMBLE_WHITESPACE + QUESTION_DESC_ONE + ANSWER_DESC_ONE + CHOICE_DESC,
            new AddMultipleChoiceQuestionCommand(expectedFlashcard));

        // multiple answers - last answer accepted
        CommandParserTestUtil.assertParseSuccess(parser,
            QUESTION_DESC_ONE + ANSWER_DESC_TWO + ANSWER_DESC_ONE + CHOICE_DESC,
            new AddMultipleChoiceQuestionCommand(expectedFlashcard));
    }

    @Test
    public void parse_multipleQnsPresent_failure() {
        // multiple questions - not accepted
        CommandParserTestUtil.assertParseFailure(parser,
                QUESTION_DESC_TWO + QUESTION_DESC_ONE + ANSWER_DESC_ONE + CHOICE_DESC,
                MESSAGE_TOO_MANY_QUESTIONS);
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
            AddMultipleChoiceQuestionCommand.MESSAGE_USAGE);

        // missing question prefix
        CommandParserTestUtil.assertParseFailure(parser, VALID_QUESTION_ONE
                + ANSWER_DESC_ONE + CHOICE_DESC,
            expectedMessage);

        // missing answer prefix
        CommandParserTestUtil.assertParseFailure(parser, QUESTION_DESC_ONE + VALID_ANSWER_ONE + CHOICE_DESC,
            expectedMessage);

        // missing Choices prefix
        CommandParserTestUtil.assertParseFailure(parser, QUESTION_DESC_ONE + ANSWER_DESC_ONE,
            expectedMessage);


        // all prefixes missing
        CommandParserTestUtil.assertParseFailure(parser, VALID_QUESTION_ONE + VALID_ANSWER_ONE,
            expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid Question
        CommandParserTestUtil.assertParseFailure(parser, ANSWER_DESC_ONE + INVALID_QUESTION_DESC + CHOICE_DESC,
            Question.MESSAGE_CONSTRAINTS);
        // invalid Answer
        CommandParserTestUtil.assertParseFailure(parser, QUESTION_DESC_THREE + INVALID_ANSWER_DESC + CHOICE_DESC,
            Answer.MESSAGE_CONSTRAINTS);


        // two invalid values, only first invalid value reported
        CommandParserTestUtil.assertParseFailure(parser, INVALID_QUESTION_DESC + INVALID_ANSWER_DESC + CHOICE_DESC,
            Question.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        CommandParserTestUtil.assertParseFailure(parser, PREAMBLE_NON_EMPTY + QUESTION_DESC_ONE
                + ANSWER_DESC_ONE + CHOICE_DESC,
            String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                AddMultipleChoiceQuestionCommand.MESSAGE_USAGE));
    }
}
