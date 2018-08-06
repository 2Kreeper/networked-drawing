package eu.barononline.networked_drawing.networking.interfaces;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.NetworkCommand;
import eu.barononline.networked_drawing.networking.CommandType;

public interface IDeleteReceiver {

    public void onDelete(@NotNull NetworkCommand<CommandType> cmd);
}
