package eu.barononline.networked_drawing.ui.interaction;

import eu.barononline.networked_drawing.ui.DrawCanvas;
import eu.barononline.networked_drawing.ui.shapes.Oval;
import eu.barononline.networked_drawing.ui.shapes.Rectangle;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static eu.barononline.networked_drawing.ui.shapes.Shapes.OVAL;
import static eu.barononline.networked_drawing.ui.shapes.Shapes.RECTANGLE;

public class UserInteractionHandler implements MouseListener, MouseMotionListener, KeyListener {

    private DrawCanvas canvas;
    private String shapeType;

    private Shape preview;
    private Point startPoint;

    public UserInteractionHandler(DrawCanvas canvas) {
        this.canvas = canvas;

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    public void setShape(String shape) {
        this.shapeType = shape;
    }

    public void onDeletePressed() {

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
                startPoint = e.getPoint();
                canvas.addPreview(preview);
            }
            canvas.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(preview != null) {
            canvas.removePreview(preview);
            //preview.fixPotentialNegativeSizes();
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
            Point delta = new Point(Math.abs(e.getPoint().x - startPoint.x), Math.abs(e.getPoint().y - startPoint.y));
            preview.setSize(delta);

            SwingUtilities.invokeLater(() -> canvas.repaint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


    /* =========== KEY LISTENER =========== */

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(Shortcuts.DELETE.matches(e)) {

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
