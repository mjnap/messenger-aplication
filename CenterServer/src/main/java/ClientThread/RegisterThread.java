package ClientThread;

import Client.ClientConnection;

import java.io.IOException;
import java.net.Socket;

public class RegisterThread extends ClientConnection implements Runnable{

    private String command;

    public RegisterThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            output.writeUTF(register(command));
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
