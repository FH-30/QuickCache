package quickcache.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import quickcache.logic.Logic;
import quickcache.logic.commands.CommandResult;
import quickcache.logic.commands.exceptions.CommandException;
import quickcache.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private final CommandExecutor commandExecutor;
    private List<String> pastCommands;
    private int pointer = 0;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.pastCommands = new ArrayList<>(16);
        pastCommands.add("");
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        try {
            String input = commandTextField.getText();

            if (pointer < pastCommands.size() - 1) {
                pastCommands = pastCommands.subList(0, pointer + 1);
                pastCommands.add("");
            }
            if (!input.isBlank()) {
                this.pastCommands.set(pastCommands.size() - 1, input);
                pastCommands.add("");
                pointer++;
            }
            commandExecutor.execute(input);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles the Up and Down arrow buttons pressed events.
     */
    @FXML
    private void handleKeyEvent() {
        commandTextField.setOnKeyPressed(event -> {
            String textToDisplay;

            switch(event.getCode()) {
            case UP:
                // handle up
                if (pointer > 0) {
                    pointer--;
                    textToDisplay = pastCommands.get(pointer);
                    commandTextField.setText(textToDisplay);
                    commandTextField.positionCaret(textToDisplay.length());
                }
                break;
            case DOWN:
                // handle down
                if (pointer < pastCommands.size() - 1) {
                    pointer++;
                    textToDisplay = pastCommands.get(pointer);
                    commandTextField.setText(textToDisplay);
                    commandTextField.positionCaret(textToDisplay.length());
                }
                break;
            default:
                // Do Nothing
            }
        });
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
