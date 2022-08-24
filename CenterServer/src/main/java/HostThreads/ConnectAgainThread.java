package HostThreads;

import Host.HostConnection;

import java.io.IOException;
import java.net.Socket;

public class ConnectAgainThread extends HostConnection implements Runnable{

    private String command;

    public ConnectAgainThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        connectAgain(command);
    }
}
