package eu.barononline.network_classes.interfaces;

import com.sun.istack.internal.NotNull;

public interface IStringOutputStream {

    //public void print(char c);

    public void print(char c);
    public void println(@NotNull String s);
    public void println(@NotNull Object o);
    public void flush();
    public void close();
}
