package eu.barononline.networked_drawing.ui.shapes;

import org.json.JSONObject;

import java.awt.*;

/**
 * <b>WARNING: CALL</b> {@code jsonSetup()} <b>FROM EVERY CONSTRUCTOR YOU IMPLEMENT!</b>
 */
public abstract class Shape {

    public static final Color SELECTION_COLOR = Color.RED;

    protected Point pos;
    protected Color color;
    protected JSONObject json;

    protected boolean filled, selected;

    public Shape(JSONObject raw) {
        JSONObject posObj = raw.getJSONObject("position");
        JSONObject colorObj = raw.getJSONObject("color");

        pos = new Point(posObj.getInt("x"), posObj.getInt("y"));
        color = new Color(colorObj.getInt("r"), colorObj.getInt("g"), colorObj.getInt("b"));
        filled = raw.getBoolean("filled");
    }

    public Shape(Point pos, Color color, boolean filled) {
        this.pos = pos;
        this.color = color;
        this.filled = filled;
    }

    protected void jsonSetup() {
        json = new JSONObject();

        json.put("position", getPositionJson());
        json.put("color", getColorJson());
        json.put("filled", filled);
    }

    private JSONObject getPositionJson() {
        JSONObject posJson = new JSONObject();
        posJson.put("x", pos.x);
        posJson.put("y", pos.y);

        return posJson;
    }

    private JSONObject getColorJson() {
        JSONObject colorJson = new JSONObject();
        colorJson.put("r", color.getRed());
        colorJson.put("g", color.getGreen());
        colorJson.put("b", color.getBlue());

        return colorJson;
    }

    public Point getPos() {
        return pos;
    }
    public Color getColor() {
        return color;
    }
    public boolean isFilled() {
        return filled;
    }
    public boolean isSelected() {
        return selected;
    }

    public void setPos(Point pos) {
        this.pos = pos;

        json.put("position", getPositionJson());
    }
    public void setColor(Color color) {
        this.color = color;
        json.put("color", getColorJson());
    }
    public void setFilled(boolean filled) {
        this.filled = filled;
        json.put("filled", filled);
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return json.toString();
    }

    protected abstract void drawUnfilled(Graphics2D g2);
    protected abstract void drawFilled(Graphics2D g2);
    public abstract boolean contains(Point p);
    public abstract String getShapeType();

    public void draw(Graphics2D g2) {
        if(selected) {
            g2.setColor(SELECTION_COLOR);
        } else {
            g2.setColor(color);
        }

        if(filled) {
            drawFilled(g2);
        } else {
            drawUnfilled(g2);
        }
    }
}
