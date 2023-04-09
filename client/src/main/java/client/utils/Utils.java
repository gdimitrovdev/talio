package client.utils;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * This class is a class for any general Utils, that do not need state.
 * So there should be nothing besides static methods in here.
 */
public class Utils {
    // Adapted from https://stackoverflow.com/a/51726678/6431494

    /**
     * Recolors an image, so that every pixel that is not transparent, becomes the target color
     *
     * @param inputImage
     * @param color
     * @return
     */
    public static Image reColor(Image inputImage, String color) {
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
}
