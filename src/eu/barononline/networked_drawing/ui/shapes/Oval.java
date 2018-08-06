package eu.barononline.networked_drawing.ui.shapes;

import org.json.JSONObject;

import java.awt.*;

public class Oval extends Shape {

    protected Point center;

    public Oval(JSONObject raw) {
        super(raw);

        init();
    }

    public Oval(Point pos, Color color, boolean filled, int width, int height) {
        super(pos, color, filled, width, height);

        init();
    }

    private void init() {
        center = new Point(pos.x + size.x / 2, pos.y + size.y / 2);

        jsonSetup();
    }

    @Override
    public void drawUnfilled(Graphics2D g2) {
        g2.drawOval(pos.x, pos.y, size.x, size.y);
    }

    @Override
    protected void drawFilled(Graphics2D g2) {
        g2.fillOval(pos.x, pos.y, size.x, size.y);
    }

    @Override
    public boolean contains(Point p) {
        double contains = (Math.pow(p.x - center.x, 2) / Math.pow(size.x / 2, 2)) + (Math.pow(p.y - center.y, 2) / Math.pow(size.y / 2, 2));

        return contains <= 1;
    }

    @Override
    public String getShapeType() {
        return Shapes.OVAL;
    }
}
