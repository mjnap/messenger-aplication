package Client;
import Host.Host;

import static Host.HostConnection.hostList;
import Host.HostConnection;
import Host.WorkSpace;
import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor
public class ClientConnection implements Serializable {

    public static List<Client> clientList = new ArrayList<>();
    public static List<WorkSpace> workSpaceList = new ArrayList<>();
    transient private Socket socket;
    transient protected DataInputStream input;
    transient protected DataOutputStream output;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    boolean checkCommandRegister(String phoneNumber){
        boolean checkPhone = clientList.stream()
                                    .map(client -> client.getPhoneNumber())
                                    .noneMatch(phone -> phone.equals(phoneNumber));
        return checkPhone;
    }

    public String register(String command){
        String phoneNumber = command.split(" ")[1],
                password = command.split(" ")[2];

        if(checkCommandRegister(phoneNumber)){
            clientList.add(new Client(phoneNumber, password, socket.getInetAddress().toString()));
            return "OK";
        }

        return "ERROR The client already register";
    }

    public String login(String command){
        String phoneNumber = command.split(" ")[1],
                password = command.split(" ")[2];

        boolean check = clientList.stream()
                                .anyMatch(client -> client.getPassword().equals(password) && client.getPhoneNumber().equals(phoneNumber));
        if(check)
            return "OK";
        return "ERROR phone number or password is wrong";
    }

    public void createWorkspace(String command) throws IOException {

        String nameOfWorkspace = command.split(" ")[1];

        // first of all user have to login
        output.writeUTF("Login:");
        output.flush();
        String resOfLogin = login(input.readUTF());

        // if login successfully, create workspace
        if(resOfLogin.equals("OK")){

            // check if workspace was already exist, send ERROR massage
            if(checkWorkspace(nameOfWorkspace)){
                Random rand = new Random();
                // select random a host
                int randHost = rand.nextInt(hostList.size());
                Host host = hostList.get(randHost);

                // select random a port and remove it
                Integer randPort = host.portList.get(rand.nextInt(host.portList.size()));
                host.portList.remove(randPort);

                // send a massage to host with its socket and get the result massage
                HostConnection hostConnection = new HostConnection(host.socket);
                hostConnection.output.writeUTF("create-workspace " + randPort +" "+ 1001);
                hostConnection.output.flush();
                String res = hostConnection.input.readUTF();
                if(res.equals("OK")){
                    workSpaceList.add(new WorkSpace(nameOfWorkspace, host, randPort));
                    output.writeUTF("OK " + host.getIp() +" "+ randPort);
                    output.flush();
                }
            }
            else {
                output.writeUTF("ERROR in create workspace");
                output.flush();
            }
        }
        else {
            output.writeUTF(resOfLogin);
            output.flush();
        }
    }

    boolean checkWorkspace(String name){

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = pattern.matcher(name);
        if(name.length() > 60 || matcher.find()){
            return false;
        }

        boolean chk;
        try{
            chk = workSpaceList.stream()
                    .map(workSpace -> workSpace.getName())
                    .noneMatch(nameW -> nameW.equals(name));
        }catch (Exception e){
            chk = true;
        }

        return chk;
    }

    /**
     * This method is a interface between client and workspace(host), that connect
     * client to the workspace. If the workspace wasn't exist or the token was invalid,
     * the method throw an exception.
     * @param command client
     * @throws IOException
     */
    public void connectToWorkspace(String command) throws IOException{
        String nameWorkspace = command.split(" ")[1];

        // first of all user have to login
        output.writeUTF("Login:");
        output.flush();
        String loginMassage = input.readUTF();
        String resOfLogin = login(loginMassage);

        // if login successfully, connect to the workspace
        if(resOfLogin.equals("OK")){
            // check that the workspace exist or not
            if(isExistWorkspace(nameWorkspace)){
                WorkSpace workSpace = workSpaceList.stream()
                        .filter(work -> work.getName().equals(nameWorkspace))
                        .collect(Collectors.toList()).get(0);

                // the temporary token is valid for 5 minutes
                String tmpToken = creatToken();
                Date time = new Date();

                // send massage to client
                output.writeUTF("OK " + workSpace.getHost().getIp() +" "+ workSpace.getPort() +" "+ tmpToken);
                output.flush();

                // receive massage from workspace(host)
                Socket tmpSocket = hostList.stream()
                                        .filter(host -> host.getIp().equals(workSpace.getHost().getIp()))
                                        .map(host -> host.socket)
                                        .collect(Collectors.toList()).get(0);
                HostConnection hostConnection = new HostConnection(tmpSocket);
                String receiveToken = hostConnection.input.readUTF();
                if(new Date().getTime() - time.getTime() > 300_000)
                    tmpToken = " ";

                if(receiveToken.equals(tmpToken)){
                    hostConnection.output.writeUTF("OK " + loginMassage.split(" ")[1]);
                    hostConnection.output.flush();
                }
                else {
                    hostConnection.output.writeUTF("ERROR token is invalid");
                    hostConnection.output.flush();
                }
            }
            else {
                output.writeUTF("ERROR the workspace doesn't exist");
                output.flush();
            }
        }
        else {
            output.writeUTF(resOfLogin);
            output.flush();
        }
    }

    boolean isExistWorkspace(String nameOfWorkspace){
        boolean check = workSpaceList.stream()
                                .map(workSpace -> workSpace.getName())
                                .anyMatch(name -> name.equals(nameOfWorkspace));
        return check;
    }

    String creatToken(){
        Random rand = new Random();
        String token = "";
        for(int i=1; i<=10; i++){
            boolean select = rand.nextBoolean();
            if(select){
                int num = rand.nextInt(10);
                token += num;
            }
            else {
                char ch = (char) (rand.nextInt(26) + 97);
                token += ch;
            }
        }

        return token;
    }
}
