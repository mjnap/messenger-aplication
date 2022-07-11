package other;
import Theards.CreateWorkspaceThread;
import Theards.WorkspaceThread;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainClass {
    public static void main(String[] args) throws IOException {

        // every host first of all connect to the central server
        Scanner scanner = new Scanner(System.in);
        String createHostCommand = scanner.nextLine();
        String ip = createHostCommand.split(" ")[1];

        Host host = new Host(ip);
        try {
            host.connection(createHostCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Executor executor = Executors.newCachedThreadPool();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String command = null;
                    try {
                        command = Host.input.readUTF();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(command.startsWith("create-workspace")) {
                        executor.execute(new CreateWorkspaceThread(command));
                        int port = Integer.parseInt(command.split(" ")[1]);
                        executor.execute(new WorkspaceThread(port, executor));
                    }
                    else if(command.startsWith("OK")){
                        WorkSpace.tmpCommand = command;
                    }
                }
            }
        }).start();
    }
}
