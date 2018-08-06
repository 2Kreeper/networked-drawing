package eu.barononline.networked_drawing.ui.interaction;

import eu.barononline.networked_drawing.ui.DrawCanvas;
import eu.barononline.networked_drawing.ui.shapes.Oval;
import eu.barononline.networked_drawing.ui.shapes.Rectangle;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static eu.barononline.networked_drawing.ui.shapes.Shapes.OVAL;
import static eu.barononline.networked_drawing.ui.shapes.Shapes.RECTANGLE;

public class UserInteractionHandler implements MouseListener, MouseMotionListener {

    private DrawCanvas canvas;
    private String shapeType;

    private Shape preview;

    public UserInteractionHandler(DrawCanvas canvas) {
        this.canvas = canvas;

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    public void setShape(String shape) {
        this.shapeType = shape;
    }

    private void tryCreateShape(Point p) {
        Shape s = makeShape(p, 50, 50);

        if(s != null) {
            canvas.add(s, false);
        }
    }

    private Shape makeShape(Point p, int width, int height) {
        Shape s = null;

        switch (shapeType) {
            case OVAL:
                s = new Oval(p, Color.BLACK, true, width, height);
                break;
            case RECTANGLE:
                s = new Rectangle(p, Color.BLACK, true, width, height);
                break;
        }

        return s;
    }

    /* ========== MOUSE LISTENER ========== */

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            Shape selected = canvas.getShape(e.getPoint());

            if (selected != null) {
                selected.setSelected(!selected.isSelected());
            } else if (preview == null) {
                preview = makeShape(e.getPoint(), 25, 25);
                canvas.addPreview(preview);
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(preview != null) {
            canvas.removePreview(preview);
            canvas.add(preview, false);

            preview = null;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    /* ====== MOUSE MOTION LISTENER ======= */

    @Override
    public void mouseDragged(MouseEvent e) {
        if(preview != null) {
            Point delta = new Point(e.getPoint().x - preview.getPos().x, e.getPoint().y - preview.getPos().y);
            preview.setSize(delta);

            SwingUtilities.invokeLater(() -> canvas.repaint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
