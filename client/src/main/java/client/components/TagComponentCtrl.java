package client.components;

import client.scenes.TagManagementCtrl;
import client.utils.ServerUtils;
import commons.Tag;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class TagComponentCtrl extends HBox {
    private final ServerUtils server;
    private final TagManagementCtrl tagManagementCtrl;
    private Tag tag;
    @FXML
    private TextField tagTitle;
    @FXML
    private ColorPicker tagForeground;
    @FXML
    private ColorPicker tagBackground;
    @FXML
    private Button buttonSave;
    @FXML
    private Button buttonDelete;

    public TagComponentCtrl(ServerUtils server, TagManagementCtrl tagManagementCtrl, Tag tag) {
        this.server = server;
        this.tagManagementCtrl = tagManagementCtrl;
        this.tag = tag;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("TagComponent.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        refreshUI();
    }

    @FXML
    protected void saveTag() {

        String  title = tagTitle.getText().trim(),
                foreground = tagForeground.getValue().toString(),
                background = tagBackground.getValue().toString();

        Tag newTag = tagManagementCtrl.makeTagFromInput(
                title, foreground, background);
        newTag.setId(tag.getId());

        if (newTag == null) {
            return;
        }

        tag = server.updateTag(newTag);
        refreshUI();
    }

    @FXML
    protected void deleteTag() {
        server.deleteTag(tag.getId());
        tagManagementCtrl.removeComponent(tag, this);
    }

    private void refreshUI() {
        tagTitle.setText(tag.getTitle());
        String[] colors = tag.getColor().split("/");
        tagForeground.setValue(Color.valueOf(colors[0]));
        tagBackground.setValue(Color.valueOf(colors[1]));
    }
}
