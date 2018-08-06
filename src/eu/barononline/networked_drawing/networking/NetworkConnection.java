package eu.barononline.networked_drawing.networking;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;
import eu.barononline.network_classes.NetworkInputStream;
import eu.barononline.network_classes.NetworkOutputStream;
import eu.barononline.network_classes.interfaces.IReceiver;
import eu.barononline.networked_drawing.main.Main;
import eu.barononline.networked_drawing.networking.interfaces.IDeleteReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IDrawReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IRedoReceiver;
import eu.barononline.networked_drawing.networking.interfaces.IUndoReceiver;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import java.util.ArrayList;

public class NetworkConnection implements IReceiver<String> {

    private NetworkInputStream in;
    private NetworkOutputStream out;

    private ArrayList<IDrawReceiver> drawReceivers = new ArrayList<>();
    private ArrayList<IUndoReceiver> undoReceivers = new ArrayList<>();
    private ArrayList<IRedoReceiver> redoReceivers = new ArrayList<>();
    private ArrayList<IDeleteReceiver> deleteReceivers = new ArrayList<>();

    public NetworkConnection(@NotNull NetworkInputStream in, @NotNull NetworkOutputStream out) {
        this.in = in;
        this.out = out;

        in.registerStringReceiver(this);
    }

    public void registerDrawReceiver(IDrawReceiver receiver) {
        drawReceivers.add(receiver);
    }
    public void registerDeleteReceiver(IDeleteReceiver receiver) {
        deleteReceivers.add(receiver);
    }
    public void registerUndoReceiver(IUndoReceiver receiver) {
        undoReceivers.add(receiver);
    }
    public void registerRedoReceiver(IRedoReceiver receiver) {
        redoReceivers.add(receiver);
    }


    private void onDraw(NetworkCommand<CommandType> cmd) {
        for(IDrawReceiver receiver : drawReceivers) {
            Main.runLater(() -> receiver.onDraw(cmd));
        }
    }
    private void onDelete(NetworkCommand<CommandType> cmd) {
        for(IDeleteReceiver receiver : deleteReceivers) {
            Main.runLater(() -> receiver.onDelete(cmd));
        }
    }
    private void onUndo(NetworkCommand<CommandType> cmd) {
        for(IUndoReceiver receiver : undoReceivers) {
            Main.runLater(() -> receiver.onUndo(cmd));
        }
    }
    private void onRedo(NetworkCommand<CommandType> cmd) {
        for(IRedoReceiver receiver : redoReceivers) {
            Main.runLater(() -> receiver.onRedo(cmd));
        }
    }


    public void sendDraw(Shape shape) {
        String body = shape.toString();
        String shapeType = shape.getShapeType();

        NetworkCommand<CommandType> cmd = new NetworkCommand<>(CommandType.Draw, body);
        cmd.addHeader(Headers.DRAW_SHAPE, shapeType);

        out.println(cmd.toString());
        System.out.println("Sent draw for shape " + shapeType);
    }

    @Override
    public void onReceive(String sent) {
        try {
            NetworkCommand<CommandType> cmd = NetworkCommand.parse(sent, CommandType.class);

            switch(cmd.getCommandType()) {
                case Draw:
                    System.out.println("Received draw for shape " + cmd.getHeader(Headers.DRAW_SHAPE));
                    onDraw(cmd);
                    break;
                case Delete:
                    onDelete(cmd);
                    break;
                case Undo:
                    onUndo(cmd);
                    break;
                case Redo:
                    onRedo(cmd);
                    break;
            }
        } catch (ClassCastException e) {
            System.err.println(e.getMessage());
        }
    }
}
