package eu.barononline.networked_drawing.ui.interaction;

import eu.barononline.networked_drawing.main.Main;
import eu.barononline.networked_drawing.ui.DrawCanvas;
import eu.barononline.networked_drawing.ui.DrawFrame;
import eu.barononline.networked_drawing.ui.shapes.Oval;
import eu.barononline.networked_drawing.ui.shapes.Rectangle;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static eu.barononline.networked_drawing.ui.shapes.Shapes.OVAL;
import static eu.barononline.networked_drawing.ui.shapes.Shapes.RECTANGLE;

public class UserInteractionHandler implements MouseListener, MouseMotionListener, KeyEventDispatcher {

    private DrawCanvas canvas;
    private String shapeType;

    private Color drawColor = Color.BLACK;

    private Shape preview;
    private Point startPoint;

    public UserInteractionHandler(DrawCanvas canvas) {
        this.canvas = canvas;

        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }

    public final Action ON_DELETE = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Main.runLater(() -> onDeletePressed());
        }
    };

    public void setShape(String shape) {
        this.shapeType = shape;
    }

    public void onDeletePressed() {
        for(Shape shape : canvas.getSelectedShapes()) {
            canvas.remove(shape, false);
        }
    }

    private Shape makeShape(Point p, int width, int height) {
        Shape s = null;

        switch (shapeType) {
            case OVAL:
                s = new Oval(p, drawColor, true, width, height);
                break;
            case RECTANGLE:
                s = new Rectangle(p, drawColor, true, width, height);
                break;
        }

        return s;
    }

    public Color getDrawColor() {
        return drawColor;
    }

    public void setDrawColor(Color drawColor) {
        this.drawColor = drawColor;
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


    /* ======= KEY EVENT DISPATCHER ======= */

    private void onKeyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_DELETE) {
            onDeletePressed();
        }
    }

    private int strokeCounter = 0,
        strokeKeyCode = 0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if(e.getKeyCode() == 0) {
            return false;
        }

        if(e.getKeyCode() == strokeKeyCode) {
            strokeCounter++;

            if(strokeCounter == 1) {
                strokeCounter = 0;

                onKeyPressed(e);
            }
        } else {
            strokeKeyCode = e.getKeyCode();
            strokeCounter = 0;
        }

        return false;
    }
}
