import Client.ClientConnection;
import Host.HostConnection;

import java.io.*;
import java.util.Scanner;

public class SaveThread implements Runnable {
    @Override
    public void run() {
        String command = new Scanner(System.in).nextLine();
        if(command.equals("shutdown")){
            try {
                save();
                sendShutdownMessage();
                Thread.sleep(1000);
                System.exit(0);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() throws IOException {
        File dataFile = new File("data");
        if(!dataFile.exists())
            dataFile.mkdirs();

        FileOutputStream fileOutput;
        BufferedOutputStream bufferedOutput;
        ObjectOutputStream objectOutput;

        // save clientList
        fileOutput = new FileOutputStream("data/clientList.txt");
        bufferedOutput = new BufferedOutputStream(fileOutput);
        objectOutput = new ObjectOutputStream(bufferedOutput);
        objectOutput.writeObject(ClientConnection.clientList);
        objectOutput.close();
        bufferedOutput.close();
        fileOutput.close();

        // save workspaceList
        fileOutput = new FileOutputStream("data/workspaceList.txt");
        bufferedOutput = new BufferedOutputStream(fileOutput);
        objectOutput = new ObjectOutputStream(bufferedOutput);
        objectOutput.writeObject(ClientConnection.workSpaceList);
        objectOutput.close();
        bufferedOutput.close();
        fileOutput.close();

        // save hostList
        fileOutput= new FileOutputStream("data/hostList.txt");
        bufferedOutput = new BufferedOutputStream(fileOutput);
        objectOutput = new ObjectOutputStream(bufferedOutput);
        objectOutput.writeObject(HostConnection.hostList);
        objectOutput.close();
        bufferedOutput.close();
        fileOutput.close();
    }

    public void sendShutdownMessage(){
        HostConnection.hostList.stream()
                .forEach(host -> {
                    try {
                        DataOutputStream output = new DataOutputStream(host.socket.getOutputStream());
                        output.writeUTF("shutdown");
                        output.flush();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
