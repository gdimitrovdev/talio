package client.scenes;

import client.components.TagComponentCtrl;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Board;
import commons.Tag;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TagManagementCtrl {
    private final ServerUtils server;
    private final MainCtrlTalio mainCtrlTalio;
    private Board board;
    private List<Tag> allTags;




    @FXML
    private TextField fieldTagTitle;
    @FXML
    private ColorPicker pickerForeground;
    @FXML
    private ColorPicker pickerBackground;
    @FXML
    private VBox tagsBox;

    @Inject
    public TagManagementCtrl(ServerUtils server, MainCtrlTalio mainCtrlTalio) {
        this.server = server;
        this.mainCtrlTalio = mainCtrlTalio;
    }

    public void initialize(Board board) {
        this.board = board;
        allTags = server.getBoard(board.getId()).getTags();
        fieldTagTitle.clear();
        pickerForeground.setValue(Color.WHITE);
        pickerBackground.setValue(Color.WHITE);

        refreshTags();
    }

    public Tag makeTagFromInput(String title, String foreground,
            String background, Tag except) {
        foreground = "#" + foreground.substring(2, 8);
        background = "#" + background.substring(2, 8);
        String tagColor = foreground + "/" + background;

        if (title.contentEquals("")) {
            mainCtrlTalio.alert("Empty Title", "The tag must have a title.");
            return null;
        }

        if (existsTagWithTitle(title, except)) {
            mainCtrlTalio.alert("Repeated Title", "A tag with this title already exists.");
            return null;
        }

        return new Tag(title, tagColor, board);
    }

    public void removeComponent(Tag tag, TagComponentCtrl child) {
        tagsBox.getChildren().remove(child);
        allTags.remove(tag);
    }

    @FXML
    protected void createTag() {

        String  tagTitle = fieldTagTitle.getText().trim(),
                foreground = pickerForeground.getValue().toString(),
                background = pickerBackground.getValue().toString();

        Tag newTag = makeTagFromInput(tagTitle, foreground, background, null);

        if (newTag == null) {
            return;
        }

        newTag = server.createTag(newTag);

        //Update list
        allTags.add(newTag);
        //Update UI component
        tagsBox.getChildren().add(new TagComponentCtrl(server, this, newTag));
    }

    @FXML
    protected void tagTitleKeyPress(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            createTag();
        }
    }

    private void refreshTags() {
        tagsBox.getChildren().clear();

        for (Tag tag : allTags) {
            tagsBox.getChildren().add(
                new TagComponentCtrl(server, this, tag));
        }
    }

    private boolean existsTagWithTitle(String tagTitle, Tag except) {
        return allTags.stream()
                .filter(t -> !t.equals(except))
                .anyMatch(t -> t.getTitle().contentEquals(tagTitle));
    }
}
