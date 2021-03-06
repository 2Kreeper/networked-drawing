package eu.barononline.networked_drawing.main;

import eu.barononline.network_classes.NetworkInputStream;
import eu.barononline.network_classes.NetworkOutputStream;
import eu.barononline.networked_drawing.networking.NetworkConnection;
import eu.barononline.networked_drawing.ui.DrawFrame;
import sun.misc.Queue;

import java.util.ConcurrentModificationException;

public class Main {

    private static Queue<Runnable> runnables = new Queue<>();

    public static void main(String[] args) {
        NetworkConnection conn = new NetworkConnection(new NetworkInputStream(23128, true),
                new NetworkOutputStream("localhost", 23129));

        DrawFrame frame = new DrawFrame("Networked Drawing", 500, 350, conn);

        while(true) {
            if(!runnables.isEmpty()) {
                try {
                    runnables.dequeue().run();
                } catch (InterruptedException | ConcurrentModificationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void runLater(Runnable r) {
        runnables.enqueue(r);
    }
}
