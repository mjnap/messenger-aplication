package other;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host implements Serializable {

    protected String ip;
    private static List<WorkSpace> workSpaceList = new ArrayList<>();
    private static transient Socket socket;
    public static transient DataInputStream input;
    public static transient DataOutputStream output;

    public Host(String ip) throws IOException {
        this.ip = ip;
        socket = new Socket(ip, 8000);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }
    public Host(){}

    /**
     * open a connection with central server for second time or more
     * @throws IOException
     */
    public void open() throws IOException {
        socket = new Socket(ip, 8000);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());

        output.writeUTF("connect-again " + ip);
        output.flush();
    }

    public void close() throws IOException {
        socket.close();
        input.close();
        output.close();
    }

    /**
     * connect to central server for the first time
     * @param command
     * @throws IOException
     */
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
            System.exit(0);
        }
    }

    /**
     * create a workspace by order central server
     * @param command
     * @throws IOException
     */
    public void createWorkspace(String command) throws IOException {
        int port = Integer.parseInt(command.split(" ")[1]);
        int ipUser = Integer.parseInt(command.split(" ")[2]);

        workSpaceList.add(new WorkSpace(port,ipUser));
        output.writeUTF("OK");
        output.flush();
    }
}
