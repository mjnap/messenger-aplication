package ClientThread;

import Client.ClientConnection;

import java.io.IOException;
import java.net.Socket;

public class ConnectWorkspaceThread extends ClientConnection implements Runnable {

    private String command;

    public ConnectWorkspaceThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            connectToWorkspace(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
