package Theards;


import Models.Client;
import Models.Host;
import Models.WorkSpace;

import java.io.IOException;
import java.net.Socket;
import java.util.List;


public class ConnectToClientThread extends WorkSpace implements Runnable {

    private String command;
    private Host host;

    public ConnectToClientThread(Socket socket, List<Client> clientList, String command, Host host) {
        super(socket, clientList);
        this.command = command;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            // first, the connection is established
            connectToClient(command, host);

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
                else if(command.startsWith("get-messages")){
                    getMessages(command);
                }
                else if(command.startsWith("edit-message")){
                    editMessage(command);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
