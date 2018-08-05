package eu.barononline.networked_drawing.ui.shapes;

import org.json.JSONObject;

import java.awt.*;

public class Rectangle extends Shape {

    protected int width, height;

    public Rectangle(JSONObject raw) {
        super(raw);

        JSONObject size = raw.getJSONObject("size");
        width = size.getInt("width");
        height = size.getInt("height");

        init();
    }

    public Rectangle(Point pos, Color color, boolean filled, int width, int height) {
        super(pos, color, filled);
        this.width = width;
        this.height = height;

        init();
    }

    private void init() {
        jsonSetup();
    }

    @Override
    protected void jsonSetup() {
        super.jsonSetup();

        json.put("size", getSizeJson());
    }

    private JSONObject getSizeJson() {
        JSONObject sizeJson = new JSONObject();

        sizeJson.put("width", width);
        sizeJson.put("height", height);

        return sizeJson;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;

        json.put("size", getSizeJson());
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;

        json.put("size", getSizeJson());
    }

    @Override
    protected void drawUnfilled(Graphics2D g2) {
        g2.drawRect((int) pos.x, (int) pos.y, width, height);
    }

    @Override
    protected void drawFilled(Graphics2D g2) {
        g2.fillRect((int) pos.x, (int) pos.y, width, height);
    }

    @Override
    public boolean contains(Point p) {
        return p.x > pos.x && p.x < pos.x + width &&
                p.y > pos.y && p.y < pos.y + height;
    }

    @Override
    public String getShapeType() {
        return Shapes.RECTANGLE;
    }
}
