package ClientThread;

import Client.ClientConnection;

import java.io.IOException;
import java.net.Socket;

public class CreatWorkspaceThread extends ClientConnection implements Runnable {

    private String command;

    public CreatWorkspaceThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            createWorkspace(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
