package eu.barononline.network_classes.interfaces;

import com.sun.istack.internal.NotNull;

import java.io.IOException;

public interface IStringInputStream {

    public char read() throws IOException;
    public @NotNull String readLine() throws IOException;
    public void close();
    public int available();
    public void registerReceiver(IReceiver<String> receiver);
}
