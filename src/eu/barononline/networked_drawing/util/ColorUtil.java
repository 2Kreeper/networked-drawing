package eu.barononline.networked_drawing.util;

import java.awt.*;

public class ColorUtil {

    public static Color complementaryColor(Color c) {
        Color complement = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());

        return complement;
    }
}
