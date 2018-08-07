package eu.barononline.network_classes;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.interfaces.IStringOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class NetworkOutputStream implements IStringOutputStream {

    private NetworkOutputStreamThread thread;
    private int port;
    private String host;

    public NetworkOutputStream(String host, int port) {
        this.port = port;
        this.host = host;

        thread = new NetworkOutputStreamThread(host, port);
        thread.start();
    }

    @Override
    public void print(char c) {
        thread.print(c);
    }

    /**
     * Sends a String via network.
     * Note: All '\u0003' and '\u0004' {@link Character}s will be removed!
     *
     * @param s The String to be sent.
     */
    @Override
    public void println(@NotNull String s) {
        if(s == null) {
            return;
        }
        s = s.replaceAll(NetworkConstants.END_OF_TEXT_CHAR + "", "")
            .replaceAll(NetworkConstants.END_OF_TRANSMISSION_CHAR + "", "");

        thread.println(s);
    }

    @Override
    public void println(@NotNull Object o) {
        if(o == null) {
            return;
        }

        println(o.toString());
    }

    /**
     * Note: USAGE IS COMPLETELY POINTLESS: data is automatically flushed
     */
    @Override
    public void flush() {
        //Thread auto-flushes, so no implementation is needed
    }

    @Override
    public void close() {
        thread.close();
    }

    private class NetworkOutputStreamThread extends Thread {

        private int port;
        private String host;
        private Socket conn;
        private boolean outputLocked = false, willClose = false;
        private ArrayBlockingQueue<Character> queue = new ArrayBlockingQueue<>(50000);

        public NetworkOutputStreamThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                while(conn == null) {
                    try {
                        conn = new Socket(host, port);
                    } catch (ConnectException e) {
                    }
                }
                System.out.println("Output connected to " + conn.getInetAddress().getHostName() + ":" + conn.getPort() + "(:" + conn.getLocalPort() + ")");
                OutputStream out = conn.getOutputStream();

                while(true) {
                    while(outputLocked); //wait until output is unlocked

                    char c = ' ';
                    ArrayList<Byte> sendBuffer = new ArrayList<>();
                    while(c != NetworkConstants.END_OF_TEXT_CHAR) {
                        try {
                            c = queue.poll();
                            sendBuffer.add((byte) c);
                            //out.write((int) c);
                        } catch (NullPointerException e) {
                            break;
                        }
                    }
                    byte[] sendArr = new byte[sendBuffer.size()];
                    for(int i = 0; i < sendArr.length; i++) {
                        sendArr[i] = sendBuffer.get(i);
                    }
                    out.write(sendArr);
                    out.flush();

                    if(willClose) {
                        out.write((int) NetworkConstants.END_OF_TRANSMISSION_CHAR);
                        out.flush();
                        conn.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void println(String s) {
            outputLocked = true;
            try {
                for (char c : s.toCharArray()) {
                    queue.put(c);
                }
                queue.put(NetworkConstants.END_OF_TEXT_CHAR);
            } catch (InterruptedException e) {
            } finally {
                outputLocked = false;
            }
        }

        public void print(char c) {
            try {
                queue.put(c);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            willClose = true;
        }
    }
}
