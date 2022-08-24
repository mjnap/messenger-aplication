import java.net.SocketException;

/**
 * This class listens to the workspace and reacts to its messages
 */
public class WorkSpaceThread implements Runnable{

    Client client;

    public WorkSpaceThread(Client client) {
        this.client = client;
        this.client.status = ClientStatus.ONLINE;
    }

    @Override
    public void run() {
        try {
            while (client.status.equals(ClientStatus.ONLINE)){
                String commandOfWorkspace = "";
                try{
                    commandOfWorkspace = client.input.readUTF();
                }catch (SocketException e){
                    client.status = ClientStatus.OFFLINE;
                }

                if(commandOfWorkspace.startsWith("OK")){
                    System.out.println(commandOfWorkspace);
                }
                else if(commandOfWorkspace.startsWith("ERROR")){
                    try{
                        throw new Exception(commandOfWorkspace);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if(commandOfWorkspace.startsWith("receive-message")){
                    System.out.println(commandOfWorkspace);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
