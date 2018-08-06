package eu.barononline.network_classes;

import com.sun.istack.internal.NotNull;
import eu.barononline.network_classes.interfaces.IStringInputStream;
import eu.barononline.network_classes.interfaces.IReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class NetworkInputStream implements IStringInputStream {

    private NetworkInputStreamThread thread;
    private ArrayList<IReceiver<String>> receivers = new ArrayList<>();

    private boolean connectionClosed = false, stayOpen;

    public NetworkInputStream(int port, boolean stayOpen) {
        this(port, 10, stayOpen);
    }

    public NetworkInputStream(int port, int backlog, boolean stayOpen) {
        this.stayOpen = stayOpen;
        thread = new NetworkInputStreamThread(port, backlog, stayOpen);
        thread.start();
    }

    @Override
    public char read() throws IOException {
        if(connectionClosed) {
            throw new ConnectionClosedException();
        }

        if(!thread.canRead()) {
            throw new IOException();
        }

        try {
            return thread.queue.poll();
        } catch (NullPointerException e) {
            return '\u0003';
        }
    }

    @Override
    public @NotNull String readLine() throws IOException {
        StringBuilder buffer = new StringBuilder();

        char read = read();
        while(read != '\u0003') {
            buffer.append(read);
            read = read();
        }

        if(buffer.length() == 0) {
            throw new IOException();
        }
        return buffer.toString();
    }

    @Override
    public void close() {
        thread.close();
    }

    @Override
    public int available() {
        return thread.queue.size();
    }

    @Override
    public void registerStringReceiver(@NotNull IReceiver<String> receiver) {
        receivers.add(receiver);
    }

    private void onReceive(String s) {
        for(IReceiver<String> receiver : receivers) {
            receiver.onReceive(s);
        }
    }

    private class NetworkInputStreamThread extends Thread {

        private int port, backlog;
        private Socket conn;
        private ArrayBlockingQueue<Character> queue = new ArrayBlockingQueue<>(50000);
        private boolean canRead = true, willClose = false, stayOpen;

        public NetworkInputStreamThread(int port, int backlog, boolean stayOpen) {
            this.port = port;
            this.backlog = backlog;
            this.stayOpen = stayOpen;
        }

        @Override
        public void run() {
            boolean close = false;
            ServerSocket server;
            try {
                server = new ServerSocket(port, backlog);
                while(!close) {
                    try {
                        conn = server.accept();
                        System.out.println("Input connected to " + conn.getInetAddress().getHostName() + ":" + conn.getPort() + "(:" + conn.getLocalPort() + ")");
                        InputStream inStream = conn.getInputStream();

                        StringBuilder buffer = new StringBuilder();
                        while (true) {
                            if (willClose) {
                                conn.close();
                                break;
                            }

                            try {
                                if (inStream.available() == 0) {
                                    continue;
                                }

                                char read = (char) inStream.read();
                                if (read == NetworkConstants.END_OF_TEXT_CHAR) {
                                    canRead = true;
                                    NetworkInputStream.this.onReceive(buffer.toString());
                                    buffer = new StringBuilder();
                                } else if (read == NetworkConstants.END_OF_TRANSMISSION_CHAR) {
                                    NetworkInputStream.this.connectionClosed = true;
                                    break;
                                } else {
                                    canRead = false;
                                    queue.put(read);
                                    buffer.append(read);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(!stayOpen || willClose) {
                        close = true;
                        server.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean canRead() {
            return canRead;
        }

        public void close() {
            willClose = true;
        }
    }
}
