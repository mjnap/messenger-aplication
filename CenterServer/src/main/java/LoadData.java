import Client.Client;
import Client.ClientConnection;
import Host.*;

import java.io.*;
import java.util.List;

public class LoadData {

    public LoadData() throws IOException, ClassNotFoundException {
        if(isExistFileData()){
            ClientConnection.clientList = loadClientList();
            HostConnection.hostList = loadHostList();
            ClientConnection.workSpaceList = loadWorkspaceList();
        }
    }

    List<Client> loadClientList() throws IOException, ClassNotFoundException {
        FileInputStream fileInput = new FileInputStream("data/clientList.txt");
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);

        List<Client> clients = (List<Client>) objectInput.readObject();

        objectInput.close();
        bufferedInput.close();
        fileInput.close();

        return clients;
    }

    List<WorkSpace> loadWorkspaceList() throws IOException, ClassNotFoundException {
        FileInputStream fileInput = new FileInputStream("data/workspaceList.txt");
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);

        List<WorkSpace> workSpaces = (List<WorkSpace>) objectInput.readObject();

        objectInput.close();
        bufferedInput.close();
        fileInput.close();

        return workSpaces;
    }

    List<Host> loadHostList() throws IOException, ClassNotFoundException {
        FileInputStream fileInput = new FileInputStream("data/hostList.txt");
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);

        List<Host> hosts = (List<Host>) objectInput.readObject();

        objectInput.close();
        bufferedInput.close();
        fileInput.close();

        return hosts;
    }

    boolean isExistFileData(){
        return new File("data").exists();
    }
}
