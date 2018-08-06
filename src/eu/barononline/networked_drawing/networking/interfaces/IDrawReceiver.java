package eu.barononline.networked_drawing.networking.interfaces;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;
import eu.barononline.networked_drawing.networking.CommandType;

public interface IDrawReceiver {

    public void onDraw(@NotNull NetworkCommand<CommandType> cmd);
}
