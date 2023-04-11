package client.components;

import client.utils.Utils;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

/**
 * The reason this extends AnchorPane instead of TextField, is that when we disable the
 * TextField, we cannot detect a mouse click, so we instead detect the mouse click on the
 * AnchorPane. This is also useful, because we can easily move the focus to the AnchorPane, when
 * Escape is pressed.
 */
public class TitleField extends AnchorPane {
    private Consumer<String> onSave;
    private Consumer<String> onFirstSave;
    private Runnable onFirstCancel;
    private String savedTitle;
    private Tooltip invalidTitleToolip;
    private Predicate<String> isValid;

    @FXML
    private TextField textField;

    public TitleField() {

    }

    /**
     * This initializer should be used if you want to initialize this title field
     * with a title, instead of letting the user enter the title.
     * The title will thus not be focused upon creation and thus onFirstSave will not be called
     *
     * @param title
     */
    public void init(String title, Consumer<String> onSave, Predicate<String> isValid) {
        init(onSave, isValid);
        setTitle(title);
        disable();
    }

    public void init(Consumer<String> onSave, Consumer<String> onFirstSave,
            Runnable onFirstCancel, Predicate<String> isValid) {
        init(onSave, isValid);
        this.onFirstSave = onFirstSave;
        this.onFirstCancel = onFirstCancel;
        Platform.runLater(this::onClick);
    }

    private void init(Consumer<String> onSave, Predicate<String> isValid) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("./TitleField.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        textField.setPromptText("Enter a title...");

        // Setting these through the FXML does not seem to work.
        // If you set them in there, the events are called on an empty TitleField, instead of on
        // this TitleField. It is very strange, but this is why we set them through code.
        this.setOnMouseClicked(me -> onClick());
        textField.setOnAction(e -> onAction());
        textField.setOnKeyPressed(e -> {
            if (KeyCode.ESCAPE == e.getCode()) {
                this.requestFocus();
            }
        });

        this.onSave = onSave;
        this.isValid = isValid;

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !this.isDisabled()) {
                onLoseFocus();
            }
        });
    }

    public void setTitle(String title) {
        this.savedTitle = title;
        textField.setText(title);
    }

    public String getTitle() {
        return savedTitle;
    }

    /**
     * This is called when a user presses enter while they are editing this title field
     */
    @FXML
    private void onAction() {
        if (!isValid.test(textField.getText())) {
            if (invalidTitleToolip != null) {
                invalidTitleToolip.hide();
            }
            Toolkit.getDefaultToolkit().beep();
            invalidTitleToolip = Utils.showTooltip(textField, "Enter a valid title");
            return;
        }
        disable();
        setTitle(textField.getText());
        // We are using onFirstSave == null as a flag for whether this is the first save or not
        if (onFirstSave == null) {
            onSave.accept(textField.getText());
        } else {
            onFirstSave.accept(textField.getText());
            onFirstSave = null;
            onFirstCancel = null;
        }
        this.requestFocus();
    }

    private void onLoseFocus() {
        disable();
        setTitle(savedTitle);
        if (onFirstSave != null) {
            onFirstCancel.run();
            onFirstCancel = null;
            onFirstSave = null;
        }
    }

    @FXML
    private void onClick() {
        enable();
        textField.requestFocus();
        textField.selectAll();
    }

    private void disable() {
        textField.setDisable(true);
        this.setCursor(Cursor.HAND);
    }

    private void enable() {
        textField.setDisable(false);
        this.setCursor(Cursor.TEXT);
    }
}
