package Theards;

import DataRepository.SaveData;
import lombok.AllArgsConstructor;
import Models.Host;
import Models.WorkSpace;

import java.util.concurrent.Executor;

@AllArgsConstructor
public class ManageThread extends SaveData implements Runnable{

    Host host;
    Executor executor;

    @Override
    public void run() {
        while (true){
            String command;
            try {
                command = host.input.readUTF();
                if(command.startsWith("create-workspace")) {
                    executor.execute(new CreateWorkspaceThread(command, host));

                    int port = Integer.parseInt(command.split(" ")[1]);
                    WorkspaceThread workspaceThread = new WorkspaceThread(port, executor, host);
                    MainClass.workspaceThreadList.add(workspaceThread);
                    executor.execute(workspaceThread);
                }
                else if(command.startsWith("OK")){
                    WorkSpace.tmpCommand = command;
                }
                else if(command.equals("shutdown")){
                    save(host);
                    System.exit(0);
                }
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }
}
