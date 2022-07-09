package main;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class WorkSpace {

    private List<Client> clientList;
    protected int port;
    private int creatorClient;
    /**
     * The user who is connected to this work
     */
    protected String userName;
    private Socket socket;
    protected DataInputStream input;
    protected DataOutputStream output;
    public static String tmpCommand = "";

    public WorkSpace(int port, int creatorClient) {
        this.port = port;
        this.creatorClient = creatorClient;
    }
    public WorkSpace(Socket socket, List<Client> clientList){
        this.socket = socket;
        this.clientList = clientList;
    }
    public WorkSpace() {}

    protected void close() throws IOException {
        socket.close();
        output.close();
        input.close();
    }

//    protected void setSocket(Socket socket) {
//        this.socket = socket;
//    }

    /**
     * This method with the help of the central server, detect a client who wants
     * connect to this workspace. The workspace ask from client a user name, if the user name
     * was already exist, the workspace send an error massage.
     * @param command
     * @throws IOException
     */
    public void connectToClient(String command) throws IOException {
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // send massage to central server for detect client
        Host.output.writeUTF(command.split(" ")[1]);
        Host.output.flush();

        // receive massage contain the client from central server
        String detectClient;
        while (tmpCommand.equals(""));
        detectClient = tmpCommand;
        tmpCommand = "";
        /********/System.out.println(detectClient);

        if(detectClient.startsWith("OK")){

            // If it is the first time that the client connects, it will be asked for a user name
            if(isFirstTime(detectClient.split(" ")[1])){
                output.writeUTF("username?");
                output.flush();

                String userName = input.readUTF();
                if(checkUserName(userName)){
                    /********/System.out.println(clientList);
                    clientList.add(new Client(userName, detectClient.split(" ")[1]));
                    /********/System.out.println(clientList);
                    output.writeUTF("OK");
                    output.flush();
                }
                else {
                    output.writeUTF("ERROR the username already exist");
                    output.flush();
                }
            }
            else {
                output.writeUTF("OK");
                output.flush();
            }
        }
        else {
            output.writeUTF(detectClient);
            output.flush();
        }
    }

    private boolean isFirstTime(String phoneNumber){
        boolean check;
        try {
            check = clientList.stream()
                            .map(client -> client.getPhoneNumber())
                            .noneMatch(phoneN -> phoneN.equals(phoneNumber));
        }catch (NullPointerException e){
            check = true;
        }

        return check;
    }

    private boolean checkUserName(String userName){
        boolean chk;
        try {
            chk = clientList.stream()
                    .map(client -> client.getUserName())
                    .noneMatch(userN -> userN.equals(userName));
        }catch (NullPointerException e){
            chk = true;
        }
        return chk;
    }

    public void sendMassage(String command){
        String userOfReceiver = command.split(" ")[1],
               JSON_massage = command.split(" ")[2];

        JSONObject jsonReceiver = (JSONObject) JSONValue.parse(JSON_massage);

    }
}
