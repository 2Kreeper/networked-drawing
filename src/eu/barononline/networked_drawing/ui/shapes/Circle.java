package eu.barononline.networked_drawing.ui.shapes;

import org.json.JSONObject;

import java.awt.*;

public class Circle extends Shape {

    protected int radius;
    protected Point center;

    public Circle(JSONObject raw) {
        super(raw);

        radius = raw.getInt("radius");

        init();
    }

    public Circle(Point pos, Color color, boolean filled, int radius) {
        super(pos, color, filled);
        this.radius = radius;

        init();
    }

    private void init() {
        center = new Point(pos.x + radius, pos.y + radius);

        jsonSetup();
    }

    @Override
    protected void jsonSetup() {
        super.jsonSetup();

        json.put("radius", radius);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        json.put("radius", radius);
    }


    @Override
    public void drawUnfilled(Graphics2D g2) {
        g2.drawOval(pos.x, pos.y, radius * 2, radius * 2);
    }

    @Override
    protected void drawFilled(Graphics2D g2) {
        g2.fillOval(pos.x, pos.y, radius * 2, radius * 2);

        g2.fillRect(center.x, center.y, 5, 5);
    }

    @Override
    public boolean contains(Point p) {
        double distance = Math.sqrt(
                Math.pow(center.x - p.x, 2) +
                        Math.pow(center.y - p.y, 2)
        );

        return distance < radius;
    }

    @Override
    public String getShapeType() {
        return Shapes.CIRCLE;
    }
}
