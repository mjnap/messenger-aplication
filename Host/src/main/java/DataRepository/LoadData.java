package DataRepository;

import Theards.WorkspaceThread;
import Models.Host;

import java.io.*;
import java.util.List;

public class LoadData {

    public static Host loadHost() throws IOException, ClassNotFoundException {
        FileInputStream fileInput = new FileInputStream("data/host.txt");
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);

        Host host = (Host) objectInput.readObject();

        objectInput.close();
        bufferedInput.close();
        fileInput.close();

        return host;
    }

    public static List<WorkspaceThread> loadWorkspaces() throws IOException, ClassNotFoundException {
        FileInputStream fileInput = new FileInputStream("data/workspaceThreads.txt");
        BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
        ObjectInputStream objectInput = new ObjectInputStream(bufferedInput);

        List<WorkspaceThread> workspaceThreads = (List<WorkspaceThread>) objectInput.readObject();

        objectInput.close();
        bufferedInput.close();
        fileInput.close();

        return workspaceThreads;
    }

    public static boolean isExistFileData(){
        return new File("data").exists();
    }
}
