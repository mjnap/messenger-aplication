package DataRepository;

import Models.Host;
import Theards.MainClass;

import java.io.*;

public class SaveData {

    public void save(Host host) throws IOException {
        File hostDirectory = new File("data");
        if(!hostDirectory.exists())
            hostDirectory.mkdirs();

        FileOutputStream fileOutput;
        BufferedOutputStream bufferedOutput;
        ObjectOutputStream objectOutput;

        // save host
        fileOutput = new FileOutputStream("data/host.txt");
        bufferedOutput = new BufferedOutputStream(fileOutput);
        objectOutput = new ObjectOutputStream(bufferedOutput);
        objectOutput.writeObject(host);
        objectOutput.close();
        bufferedOutput.close();
        fileOutput.close();
        host.close();

        // save workspaceThreadList in a file
        fileOutput = new FileOutputStream("data/workspaceThreads.txt");
        bufferedOutput = new BufferedOutputStream(fileOutput);
        objectOutput = new ObjectOutputStream(bufferedOutput);
        objectOutput.writeObject(MainClass.workspaceThreadList);
        objectOutput.close();
        bufferedOutput.close();
        fileOutput.close();
    }
}
