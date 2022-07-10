package Theards;

import main.Client;
import main.WorkSpace;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ConnectToClientThread extends WorkSpace implements Runnable {

    private String command;

    public ConnectToClientThread(Socket socket, List<Client> clientList, String command) {
        super(socket, clientList);
        this.command = command;
    }

    @Override
    public void run() {
        try {
            // first, the connection is established
            connectToClient(command);

            /* then the workspace keep the connection with client until
             the client send "disconnect" */
            while (!command.equals("disconnect")){

                command = input.readUTF();
                if(command.equals("disconnect")){
                    close();
                }
                else if(command.startsWith("send-message")){
                    sendMassage(command);
                }
                else if(command.equals("get-chats")){
                    getChats();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
