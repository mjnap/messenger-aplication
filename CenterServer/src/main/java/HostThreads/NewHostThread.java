package HostThreads;

import Host.HostConnection;

import java.io.IOException;
import java.net.Socket;

public class NewHostThread extends HostConnection implements Runnable{

    private String command;

    public NewHostThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            connectToHost(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
