package other;
import Theards.CreateWorkspaceThread;
import Theards.WorkspaceThread;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainClass {

    static List<WorkspaceThread> workspaceThreadList = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);
        Executor executor = Executors.newCachedThreadPool();
        Host host;

        if(isExitsFileData()){
            host = fetchHost();
            host.open();
            workspaceThreadList = fetchWorkspaces();
            workspaceThreadList.stream().forEach(workspaceThread -> {
                workspaceThread.setExecutor(executor);
                executor.execute(workspaceThread);
            });
            System.out.println("OK");
        }
        else {
            // every host first of all connect to the central server
            String createHostCommand = scanner.nextLine();
            String ip = createHostCommand.split(" ")[1];

            host = new Host(ip);
            try {
                host.connection(createHostCommand);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // thread for get shutdown command
        Host finalHost = host;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String command = "";
                while (!command.equals("shutdown")){
                    command = scanner.next();
                    if(command.equals("shutdown")){
                        try {
                            save(finalHost);
                            System.exit(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

        // thread for manage command that sent from central service
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    String command = null;
                    try {
                        command = Host.input.readUTF();
                        if(command.startsWith("create-workspace")) {
                            executor.execute(new CreateWorkspaceThread(command));

                            int port = Integer.parseInt(command.split(" ")[1]);
                            WorkspaceThread workspaceThread = new WorkspaceThread(port, executor);
                            workspaceThreadList.add(workspaceThread);
                            executor.execute(workspaceThread);
                        }
                        else if(command.startsWith("OK")){
                            WorkSpace.tmpCommand = command;
                        }
                        else if(command.equals("shutdown")){
                            save(finalHost);
                            System.exit(0);
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            }
        }).start();
    }

    public static void save(Host host) throws IOException {
        File hostDirectory = new File("data");
        if(!hostDirectory.exists())
            hostDirectory.mkdirs();

        // save host
        FileOutputStream fileOutputHost = new FileOutputStream("data/host.txt");
        ObjectOutputStream objectOutputHost = new ObjectOutputStream(fileOutputHost);
        objectOutputHost.writeObject(host);
        fileOutputHost.close();
        objectOutputHost.close();
        host.close();

        // save workspaceThreadList in a file
        FileOutputStream fileOutputWorkspace = new FileOutputStream("data/workspaceThreads.txt");
        ObjectOutputStream objectOutputWorkspace = new ObjectOutputStream(fileOutputWorkspace);
        objectOutputWorkspace.writeObject(workspaceThreadList);
        fileOutputWorkspace.close();
        objectOutputWorkspace.close();
    }

    public static Host fetchHost() throws IOException, ClassNotFoundException {
        FileInputStream fileInputHost = new FileInputStream("data/host.txt");
        ObjectInputStream objectInputHost = new ObjectInputStream(fileInputHost);

        Host host = (Host) objectInputHost.readObject();

        fileInputHost.close();
        objectInputHost.close();

        return host;
    }

    public static List<WorkspaceThread> fetchWorkspaces() throws IOException, ClassNotFoundException {
        FileInputStream fileInputWorkspace = new FileInputStream("data/workspaceThreads.txt");
        ObjectInputStream objectInputWorkspace = new ObjectInputStream(fileInputWorkspace);

        List<WorkspaceThread> workspaceThreads = (List<WorkspaceThread>) objectInputWorkspace.readObject();

        fileInputWorkspace.close();
        objectInputWorkspace.close();

        return workspaceThreads;
    }

    public static boolean isExitsFileData(){
        return new File("data").exists();
    }
}
