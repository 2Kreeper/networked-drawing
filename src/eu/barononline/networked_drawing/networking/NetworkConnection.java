package eu.barononline.networked_drawing.networking;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;
import eu.barononline.network_classes.NetworkInputStream;
import eu.barononline.network_classes.NetworkOutputStream;
import eu.barononline.network_classes.interfaces.IReceiver;
import eu.barononline.networked_drawing.main.Main;
import eu.barononline.networked_drawing.ui.shapes.Shape;

import java.util.ArrayList;

public class NetworkConnection implements IReceiver<String> {

    private NetworkInputStream in;
    private NetworkOutputStream out;

    private ArrayList<IDrawReceiver> drawReceivers = new ArrayList<>();
    private ArrayList<IReceiver<NetworkCommand<CommandType>>> commandReceivers = new ArrayList<>();

    public NetworkConnection(@NotNull NetworkInputStream in, @NotNull NetworkOutputStream out) {
        this.in = in;
        this.out = out;

        in.registerReceiver(this);
    }

    public void registerDrawReceiver(IDrawReceiver receiver) {
        drawReceivers.add(receiver);
    }

    private void onDraw(NetworkCommand<CommandType> cmd) {
        for(IDrawReceiver receiver : drawReceivers) {
            Main.runLater(() -> receiver.onDraw(cmd));
        }
    }

    public void registerCommandReceiver(IReceiver<NetworkCommand<CommandType>> receiver) {
        commandReceivers.add(receiver);
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
            System.out.println("String received:");
            System.out.println(sent + "\n");

            if (cmd.getCommandType() == CommandType.Draw) {
                System.out.println("Received draw for shape " + cmd.getHeader(Headers.DRAW_SHAPE));
                onDraw(cmd);
            } else {
                for (IReceiver<NetworkCommand<CommandType>> receiver : commandReceivers) {
                    Main.runLater(() -> receiver.onReceive(cmd));
                }
            }
        } catch (ClassCastException e) {
            System.err.println(e.getMessage());
        }
    }
}
