package Models;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

@SuperBuilder
public class WorkSpace implements Serializable {

    @Getter
    private List<Client> clientList;
    protected int port;
    private int creatorClient;
    /**
     * The user who is connected to this workspace
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

    protected void close() throws IOException {
        socket.close();
        output.close();
        input.close();
        Client thisClient = clientList.stream()
                                    .filter(client -> client.getUserName().equals(userName))
                                    .collect(Collectors.toList()).get(0);
        thisClient.status = ClientStatus.OFFLINE;
        for(int i=0; i<clientList.size(); i++)
            if(clientList.get(i).equals(thisClient)) {
                clientList.set(i, thisClient);
                break;
            }
    }

    /**
     * This method with the help of the central server, detect a client who wants
     * connect to this workspace. The workspace ask from client a user name, if the user name
     * was already exist, the workspace send an error massage.
     * @param command
     * @throws IOException
     */
    public void connectToClient(String command, Host host) throws IOException {
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        // send massage to central server for detect client
        host.output.writeUTF(command.split(" ")[1]);
        host.output.flush();

        // receive massage contain the client from central server
        String detectClient;
        while (tmpCommand.equals(""));
        detectClient = tmpCommand;
        tmpCommand = "";

        if(detectClient.startsWith("OK")){

            // If it is the first time that the client connects, it will be asked for a user name
            if(isFirstTime(detectClient.split(" ")[1])){
                output.writeUTF("username?");
                output.flush();

                String userName = input.readUTF();
                if(checkUserName(userName)){
                    Client newClient = new Client(userName, detectClient.split(" ")[1]);
                    newClient.setConnection(socket);
                    newClient.status = ClientStatus.ONLINE;
                    clientList.add(newClient);
                    this.userName = userName;
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

                Client thisClient = clientList.stream()
                                            .filter(client -> client.getPhoneNumber().equals(detectClient.split(" ")[1]))
                                             .collect(Collectors.toList()).get(0);
                this.userName = thisClient.getUserName();

                thisClient.setConnection(socket);
                thisClient.status = ClientStatus.ONLINE;
                for(int i=0; i<clientList.size(); i++)
                    if(clientList.get(i).equals(thisClient)){
                        clientList.set(i, thisClient);
                    }
            }
        }
        else {
            output.writeUTF(detectClient);
            output.flush();
        }
    }

    boolean isFirstTime(String phoneNumber){
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

    boolean checkUserName(String userName){
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

    public void sendMassage(String command) throws IOException {
        String userOfReceiver = command.split(" ")[1],
               JSON_massage = "{" + command.split("[{]")[1];

        // check whether there is a receiving user or not
        if(isUserExist(userOfReceiver)){
            // create right format JSON
            JSONObject jsonReceiver = (JSONObject) JSONValue.parse(JSON_massage);

            Client thisClient = clientList.stream()
                    .filter(client -> client.getUserName().equals(userName))
                    .collect(Collectors.toList()).get(0);

            JSONObject newJSON = new JSONObject();
            newJSON.put("seq", thisClient.getSeq(userOfReceiver)+1);
            newJSON.put("from", userName);
            newJSON.put("type", jsonReceiver.get("type"));
            newJSON.put("body", jsonReceiver.get("body"));

            // save massage for this client
            thisClient.saveMassage(userOfReceiver, new Massage(newJSON, MassageStatus.SENDER));

            // send seq to this client
            output.writeUTF("OK " + thisClient.getSeq(userOfReceiver));
            output.flush();

            // send massage to other client if status was online
            Client otherClient = clientList.stream()
                    .filter(client -> client.getUserName().equals(userOfReceiver))
                    .collect(Collectors.toList()).get(0);

            if(otherClient.status.equals(ClientStatus.ONLINE)){
                otherClient.output.writeUTF("receive-message " + userName +" "+ newJSON);
                otherClient.output.flush();

                // save massage for other client
                otherClient.saveMassage(userName, new Massage(newJSON, MassageStatus.SEEN));
            }
            else {
                otherClient.saveMassage(userName, new Massage(newJSON, MassageStatus.UNSEEN));
            }
        }
        else {
            output.writeUTF("ERROR there is no receiving user");
            output.flush();
        }
    }

    boolean isUserExist(String userName){
        try{
            boolean check = clientList.stream()
                                    .map(client -> client.getUserName())
                                    .anyMatch(name -> name.equals(userName));
            return check;
        }catch (NullPointerException e){
            return false;
        }
    }

    public void getChats() throws IOException {
        String res = clientList.stream()
                            .filter(client -> client.getUserName().equals(userName))
                            .collect(Collectors.toList()).get(0).getChats();

        output.writeUTF("OK " + res);
        output.flush();
    }

    public void getMessages(String command) throws IOException {
        String goalUser = command.split(" ")[1];
        String res = clientList.stream()
                            .filter(client -> client.getUserName().equals(userName))
                            .collect(Collectors.toList()).get(0).getMessages(goalUser);


        output.writeUTF(res);
        output.flush();
    }

    public void editMessage(String command) throws IOException {
        String goalUser = command.split(" ")[1];
        JSONObject infoEdit = (JSONObject) JSONValue.parse("{" + command.split("[{]")[1]);

        long seq = (long) infoEdit.get("seq");
        String newBody = (String) infoEdit.get("newBody");


        // edit message for this user
        String res = clientList.stream()
                            .filter(client -> client.getUserName().equals(userName))
                            .collect(Collectors.toList()).get(0).editMessage(goalUser,seq,newBody,true);
        if(res.startsWith("OK")){
            // edit message for goal user
            clientList.stream()
                    .filter(client -> client.getUserName().equals(goalUser))
                    .collect(Collectors.toList()).get(0).editMessage(userName,seq,newBody,false);
        }

        output.writeUTF(res);
        output.flush();
    }
}
