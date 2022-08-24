package ClientThread;

import Client.ClientConnection;

import java.io.IOException;
import java.net.Socket;

public class LoginThread extends ClientConnection implements Runnable{

    private String command;

    public LoginThread(Socket socket, String command) throws IOException {
        super(socket);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            output.writeUTF(login(command));
            output.flush();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
