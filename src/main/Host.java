package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host {

    protected String ip;
    private static List<WorkSpace> workSpaceList = new ArrayList<>();
    private static Socket socket;
    public static DataInputStream input;
    public static DataOutputStream output;

    public Host(String ip) throws IOException {
        this.ip = ip;
        socket = new Socket(ip, 8000);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }
    public Host(){}

    public void connection(String command) throws IOException {

        try {
            // send creat-host command to central server
            output.writeUTF(command);
            output.flush();
            // get message result of central server
            String message = input.readUTF();
            if(message.startsWith("OK")){
                output.writeUTF("check");
                output.flush();

                int port = Integer.parseInt(message.split(" ")[1]);
                long code = 0;

                // create temp connection to check the code
                try (ServerSocket serverSocket = new ServerSocket(port);
                     Socket tmpSocket = serverSocket.accept();
                     DataInputStream tmpInput = new DataInputStream(tmpSocket.getInputStream())){

                    code = Long.parseLong(tmpInput.readUTF().split(" ")[1]);
                }

                output.writeLong(code);
                output.flush();

                String resOfCheckCode = input.readUTF();
                if(resOfCheckCode.equals("OK")){
                    System.out.println("OK");
                }
                else {
                    throw new Exception(resOfCheckCode);
                }
            }
            else {
                throw new Exception(message);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createWorkspace(String command) throws IOException {
        int port = Integer.parseInt(command.split(" ")[1]);
        int ipUser = Integer.parseInt(command.split(" ")[2]);

        workSpaceList.add(new WorkSpace(port,ipUser));
        output.writeUTF("OK");
        output.flush();
    }
}
