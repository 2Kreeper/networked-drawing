package eu.barononline.networked_drawing.networking;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;

public interface IDrawReceiver {

    public void onDraw(@NotNull NetworkCommand<CommandType> cmd);
}
