package Theards;

import DataRepository.LoadData;
import Models.Host;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainClass {

    public static List<WorkspaceThread> workspaceThreadList = new ArrayList<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);
        Executor executor = Executors.newCachedThreadPool();
        Host host;

        if(LoadData.isExistFileData()){
            host = LoadData.loadHost();
            host.open();
            workspaceThreadList = LoadData.loadWorkspaces();
            workspaceThreadList.stream().forEach(workspaceThread -> {
                workspaceThread.setExecutor(executor);
                workspaceThread.setHost(host);
                executor.execute(workspaceThread);
            });
            System.out.println("OK");
        }
        else {
            // connect to central server
            String createHostCommand = scanner.nextLine();
            String ip = createHostCommand.split(" ")[1];

            host = new Host(ip);
            try{
                host.connection(createHostCommand);
            }catch (Exception e){
                e.printStackTrace();
                System.exit(0);
            }
        }

        // thread for receive shutdown command
        new Thread(new ShutdownGiverThread(scanner, host)).start();

        // thread for manage command that sent from central service
        new Thread(new ManageThread(host, executor)).start();
    }
}
