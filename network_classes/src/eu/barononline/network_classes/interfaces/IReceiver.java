package eu.barononline.network_classes.interfaces;

import com.sun.istack.internal.NotNull;

public interface IReceiver<T> {

    public void onReceive(@NotNull T sent);
}
