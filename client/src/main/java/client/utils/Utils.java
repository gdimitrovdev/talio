package client.utils;

import commons.Card;
import commons.Subtask;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javafx.animation.PauseTransition;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class is a class for any general Utils, that do not need state.
 * So there should be nothing besides static methods in here.
 */
public class Utils {

    /**
     * Recolors an image, so that every pixel that is not transparent, becomes the target color
     *
     * @param inputImage
     * @param color
     * @return
     */
    public static Image reColor(Image inputImage, String color) {
        // Adapted from https://stackoverflow.com/a/51726678/6431494
        Color newColor = Color.web(color);
        int width = (int) inputImage.getWidth();
        int height = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(width, height);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        int nb = (int) newColor.getBlue() * 255;
        int nr = (int) newColor.getRed() * 255;
        int ng = (int) newColor.getGreen() * 255;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (a != 0) {
                    r = nr;
                    g = ng;
                    b = nb;
                }
                argb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, argb);
            }
        }
        return outputImage;
    }

    /**
     * @param color color in format #XXXXXX/#XXXXXX/OPTIONAL_NAME
     * @return the background color
     */
    public static String getBackgroundColor(String color) {
        return color.split("/")[0];
    }

    /**
     * @param color color in format #XXXXXX/#XXXXXX/OPTIONAL_NAME
     * @return the foreground color
     */
    public static String getForegroundColor(String color) {
        return color.split("/")[1];
    }

    /**
     * The color schemes also need a name, so the easiest way to add the name is to append it
     * to the end of the color of the scheme, since that was already a single string in the
     * format #XXXXXX/#XXXXXX
     *
     * @param color color in format #XXXXXX/#XXXXXX/COLOR_SCHEME_NAME
     * @return the name of the color scheme
     */
    public static String getColorSchemeName(String color) {
        return color.split("/")[2];
    }

    public static Tooltip showTooltip(Control control, String tooltipText) {
        // Adapted from https://stackoverflow.com/a/19938766/6431494
        // and https://stackoverflow.com/a/64724602/6431494
        Point2D p = control.localToScene(0, 0);
        final Tooltip customTooltip = new Tooltip();
        customTooltip.setText(tooltipText);
        customTooltip.show(control.getScene().getWindow(),
                p.getX() + control.getScene().getX() + control.getScene().getWindow().getX(),
                p.getY() + control.getHeight() + control.getScene().getY() + control.getScene()
                        .getWindow().getY()
        );
        customTooltip.setAutoHide(true);
        PauseTransition pt = new PauseTransition(Duration.millis(2000));
        pt.setOnFinished(e -> {
            customTooltip.hide();
        });
        pt.play();
        return customTooltip;
    }

    /**
     * Creates a new subtask and places it at the end of the given card.
     * Beware, this also modifies the card's list of subtasks.
     *
     * @param card
     * @param title
     * @return a subtask with a null Id, since it has not yet been created on the server
     */
    public static Subtask createNewSubtask(Card card, String title) {
        Subtask subtask = new Subtask(title, card, false);
        subtask.setPositionInCard(0L);
        subtask.setId(null);
        if (card.getSubtasks().size() != 0) {
            subtask.setPositionInCard(card.getSubtasks().stream()
                    .max(Comparator.comparing(Subtask::getPositionInCard)).get().getPositionInCard()
                    + 1L);
        }
        card.addSubtask(subtask);
        return subtask;
    }

    /**
     * @param card
     * @return the subtasks of the card, sorted according to their positionInCard
     */
    public static List<Subtask> getSubtasksSorted(Card card) {
        return card.getSubtasks().stream().sorted(Comparator.comparing(
                Subtask::getPositionInCard)).toList();
    }

    /**
     * If possible, swaps the subtask with the next subtask that has a lower priority
     *
     * @param subtask a subtask with a valid card that has a valid list of subtasks
     * @return Whether the subtask had the lowest priority of all subtasks in its card
     */
    public static boolean subtaskMoveUp(Subtask subtask) {
        Optional<Subtask> upSubtask = subtask.getCard().getSubtasks().stream()
                .filter(s -> s.getPositionInCard() < subtask.getPositionInCard())
                .max(Comparator.comparing(Subtask::getPositionInCard));
        if (upSubtask.isPresent()) {
            Long upSubtaskPosition = upSubtask.get().getPositionInCard();
            upSubtask.get().setPositionInCard(subtask.getPositionInCard());
            subtask.setPositionInCard(upSubtaskPosition);
            return true;
        }
        return false;
    }

    /**
     * If possible, swaps the subtask with the next subtask that has a higher priority
     *
     * @param subtask a subtask with a valid card that has a valid list of subtasks
     * @return Whether the subtask had the lowest priority of all subtasks in its card
     */
    public static boolean subtaskMoveDown(Subtask subtask) {
        Optional<Subtask> downSubtask = subtask.getCard().getSubtasks().stream()
                .filter(s -> s.getPositionInCard() > subtask.getPositionInCard())
                .min(Comparator.comparing(Subtask::getPositionInCard));
        if (downSubtask.isPresent()) {
            Long upSubtaskPosition = downSubtask.get().getPositionInCard();
            downSubtask.get().setPositionInCard(subtask.getPositionInCard());
            subtask.setPositionInCard(upSubtaskPosition);
            return true;
        }
        return false;
    }

    public static void subtaskComponentMoveUp(HBox subtaskElement, Subtask subtask,
            VBox subtasksContainer) {
        if (Utils.subtaskMoveUp(subtask)) {
            int indexOfSubtaskUp =
                    subtasksContainer.getChildren().indexOf(subtaskElement) - 1;
            subtasksContainer.getChildren().remove(subtaskElement);
            subtasksContainer.getChildren().add(indexOfSubtaskUp, subtaskElement);
        }
    }

    public static void subtaskComponentMoveDown(HBox subtaskElement, Subtask subtask,
            VBox subtasksContainer) {
        if (Utils.subtaskMoveDown(subtask)) {
            int indexOfSubtaskDown =
                    subtasksContainer.getChildren().indexOf(subtaskElement) + 1;
            var subtaskDown = subtasksContainer.getChildren().get(indexOfSubtaskDown);
            subtasksContainer.getChildren().remove(subtaskDown);
            subtasksContainer.getChildren().add(indexOfSubtaskDown - 1, subtaskDown);
        }
    }
}
