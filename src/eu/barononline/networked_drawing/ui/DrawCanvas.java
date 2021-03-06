package eu.barononline.networked_drawing.ui;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;
import eu.barononline.networked_drawing.networking.CommandType;
import eu.barononline.networked_drawing.networking.Headers;
import eu.barononline.networked_drawing.networking.NetworkConnection;
import eu.barononline.networked_drawing.networking.interfaces.IDeleteReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IDrawReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IRedoReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IUndoReceiver;
import eu.barononline.networked_drawing.ui.interaction.UserInteractionHandler;
import eu.barononline.networked_drawing.ui.shapes.Oval;
import eu.barononline.networked_drawing.ui.shapes.Rectangle;
import eu.barononline.networked_drawing.ui.shapes.Shape;
import eu.barononline.networked_drawing.ui.shapes.Shapes;
import org.json.JSONObject;
import sun.misc.Queue;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;

public class DrawCanvas extends JPanel implements IDrawReceiver, IUndoReceiver, IRedoReceiver, IDeleteReceiver {

    private ArrayList<Shape> shapes = new ArrayList<>();
    private ArrayList<Shape> previewShapes = new ArrayList<>();
    private Stack<Shape> undones = new Stack<>();

    private UserInteractionHandler handler;
    private NetworkConnection conn;

    public DrawCanvas(NetworkConnection conn) {
        super();
        this.conn = conn;

        handler = new UserInteractionHandler(this);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        clear(g2);

        for(Shape shape : shapes) {
            shape.draw(g2);
        }

        for(Shape shape : previewShapes) {
            shape.draw(g2);
        }
    }

    public Shape getShape(@NotNull Point pos) {
        Shape result = null;

        for(Shape shape : shapes) {
            if(shape.contains(pos)) {
                result = shape;
            }
        }

        return result;
    }

    public void addPreview(@NotNull Shape s) {
        previewShapes.add(s);

        repaint();
    }

    public void removePreview(@NotNull Shape s) {
        previewShapes.remove(s);

        repaint();
    }

    public void add(@NotNull Shape s, boolean overNetwork) {
        shapes.add(s);

        if(overNetwork) {
            s.setColor(Color.blue);
        }

        if(!overNetwork) {
            conn.sendDraw(s);
        }

        repaint();
    }

    public void remove(@NotNull Shape s, boolean overNetwork) {
        shapes.remove(s);

        if(!overNetwork) {
            conn.sendDelete(s);
        }

        repaint();
    }

    public Shape[] getSelectedShapes() {
        ArrayList<Shape> selected = new ArrayList<>();

        for(Shape s : shapes) {
            if(s.isSelected()) {
                selected.add(s);
            }
        }

        return selected.toArray(new Shape[0]);
    }

    private void clear(@NotNull Graphics2D g2) {
        g2.setColor(getBackground());
        g2.clearRect(0, 0, getWidth(), getHeight());
    }

    public UserInteractionHandler getHandler() {
        return handler;
    }

    @Override
    public void onDraw(@NotNull NetworkCommand<CommandType> cmd) {
        if(!cmd.containsHeader(Headers.DRAW_SHAPE)) {
            System.err.println("Draw Shape not specified in Draw command!");
            return;
        }

        JSONObject body = new JSONObject(cmd.getBody());

        switch(cmd.getHeader(Headers.DRAW_SHAPE)) {
            case Shapes.OVAL:
                add(new Oval(body), true);
                break;
            case Shapes.RECTANGLE:
                add(new Rectangle(body), true);
                break;
        }
    }

    @Override
    public void onDelete(NetworkCommand<CommandType> cmd) {
        JSONObject body = new JSONObject(cmd.getBody());
        UUID deleteId = UUID.fromString(body.getString("id"));

        for(Shape s : shapes) {
            if(s.getUuid().equals(deleteId)) {
                remove(s, true);
                break;
            }
        }

        repaint();
    }

    @Override
    public void onRedo(NetworkCommand<CommandType> cmd) {
        shapes.add(undones.pop());

        repaint();
    }

    @Override
    public void onUndo(NetworkCommand<CommandType> cmd) {
        Shape undone = shapes.remove(shapes.size() - 1);
        undones.push(undone);

        repaint();
    }
}
