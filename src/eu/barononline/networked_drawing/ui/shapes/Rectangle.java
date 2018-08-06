package eu.barononline.networked_drawing.ui.shapes;

import org.json.JSONObject;

import java.awt.*;

public class Rectangle extends Shape {

    public Rectangle(JSONObject raw) {
        super(raw);

        init();
    }

    public Rectangle(Point pos, Color color, boolean filled, int width, int height) {
        super(pos, color, filled, width, height);

        init();
    }

    private void init() {
        jsonSetup();
    }

    @Override
    protected void drawUnfilled(Graphics2D g2) {
        g2.drawRect((int) pos.x, (int) pos.y, size.x, size.y);
    }

    @Override
    protected void drawFilled(Graphics2D g2) {
        g2.fillRect((int) pos.x, (int) pos.y, size.x, size.y);
    }

    @Override
    public boolean contains(Point p) {
        return p.x > pos.x && p.x < pos.x + size.x &&
                p.y > pos.y && p.y < pos.y + size.y;
    }

    @Override
    public String getShapeType() {
        return Shapes.RECTANGLE;
    }
}
