import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client{

    private String ip;
    ClientStatus status;
    private Socket socket;
    DataInputStream input;
    DataOutputStream output;

    public void open(int port) throws IOException {
        socket = new Socket(ip, port);
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void close() throws IOException {
        socket.close();
        input.close();
        output.close();
    }

    public void register(Scanner scanner) throws Exception {
        open(8000);

        String commandRegister = scanner.nextLine();
        output.writeUTF(commandRegister);
        output.flush();
        String resRegister = input.readUTF();

        close();

        if(resRegister.startsWith("ERROR"))
            throw new Exception(resRegister);
        else
            System.out.println(resRegister);
    }

    public void createWorkspace(String command, Scanner scanner) throws IOException {
        open(8000);
        // send create-workspace command
        output.writeUTF(command);
        output.flush();

        // send information for login
        System.out.print(input.readUTF() + " ");
        output.writeUTF(scanner.nextLine());
        output.flush();

        // receive result from central server
        String resOfCreateWorkspace = input.readUTF();
        close();

        try{
            if(resOfCreateWorkspace.startsWith("OK"))
                System.out.println(resOfCreateWorkspace);
            else
                throw new Exception(resOfCreateWorkspace);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connectToWorkspace(String command, Scanner scanner) throws IOException {
        open(8000);
        // send connect-workspace command
        output.writeUTF(command);
        output.flush();

        // send information for login
        System.out.print(input.readUTF() + " ");
        output.writeUTF(scanner.nextLine());
        output.flush();

        String res = input.readUTF();
        close();

        try {
            if(res.startsWith("OK")){
                String[] strs = res.split(" ");
                String  port = strs[2],
                        token = strs[3];

                open(Integer.parseInt(port));
                output.writeUTF("connect " + token);
                output.flush();

                String massageOfWorkspace = input.readUTF();

                if(massageOfWorkspace.equals("username?")){
                    System.out.print("User name : ");
                    output.writeUTF(new Scanner(System.in).next());
                    output.flush();

                    String resOfUserName = input.readUTF();
                    if(resOfUserName.startsWith("ERROR"))
                        throw new Exception(resOfUserName);
                    else
                        System.out.println(resOfUserName);
                }
                else if(massageOfWorkspace.equals("OK")){
                    System.out.println(massageOfWorkspace);
                }
                else {
                    throw new Exception(massageOfWorkspace);
                }
            }
            else {
                throw new Exception(res);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnectWorkspace() throws IOException {
        try{
            output.writeUTF("disconnect");
            output.flush();
            close();
        }catch (SocketException e){
            e.getStackTrace();
        }
        finally {
            status = ClientStatus.OFFLINE;
        }
    }

    public void sendMassage(String command) throws IOException {
        // send command to workspace
        output.writeUTF(command);
        output.flush();
    }

    public void getChats() throws IOException {
        output.writeUTF("get-chats");
        output.flush();
    }

    public void getMessages(String command) throws IOException {
        output.writeUTF(command);
        output.flush();
    }

    public void editMessage(String command) throws IOException {
        output.writeUTF(command);
        output.flush();
    }
}

enum ClientStatus {
    ONLINE , OFFLINE
}
