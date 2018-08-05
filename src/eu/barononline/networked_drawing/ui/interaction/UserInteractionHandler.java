package eu.barononline.networked_drawing.ui.interaction;

import eu.barononline.networked_drawing.ui.DrawCanvas;
import eu.barononline.networked_drawing.ui.shapes.Circle;
import eu.barononline.networked_drawing.ui.shapes.Rectangle;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static eu.barononline.networked_drawing.ui.shapes.Shapes.CIRCLE;
import static eu.barononline.networked_drawing.ui.shapes.Shapes.RECTANGLE;

public class UserInteractionHandler implements MouseListener {

    private DrawCanvas canvas;
    private String shapeType;

    public UserInteractionHandler(DrawCanvas canvas) {
        this.canvas = canvas;

        canvas.addMouseListener(this);
    }

    public void setShape(String shape) {
        this.shapeType = shape;
    }

    private void tryCreateShape(Point p) {
        Shape s = makeShape(p);

        if(s != null) {
            canvas.add(s, false);
        }
    }

    private Shape makeShape(Point p) {
        Shape s = null;

        switch (shapeType) {
            case CIRCLE:
                s = new Circle(p, Color.BLACK, true, 25);
                break;
            case RECTANGLE:
                s = new Rectangle(p, Color.BLACK, true, 50, 50);
                break;
        }

        return s;
    }

    /* ========== MOUSE LISTENER ========== */

    @Override
    public void mouseClicked(MouseEvent e) {
        Shape selected = canvas.getShape(e.getPoint());

        if(selected == null) {
            tryCreateShape(e.getPoint());
        } else {
            selected.setSelected(!selected.isSelected());
        }

        canvas.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
