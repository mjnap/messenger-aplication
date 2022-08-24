import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainClass {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        Executor executor = Executors.newCachedThreadPool();

         Client client = new Client();

         String command;

         while (true){
             command = scanner.nextLine();
             try {
                 if(command.equals("register")){
                     client.register(scanner);
                 }
                 else if(command.startsWith("create-workspace")){
                     client.createWorkspace(command, scanner);
                 }
                 else if(command.startsWith("connect-workspace")){
                     client.connectToWorkspace(command, scanner);
                     executor.execute(new WorkSpaceThread(client));
                 }
                 else if(command.equals("disconnect")){
                     client.disconnectWorkspace();
                     System.out.println("OK disconnect done");
                 }
                 else if(command.startsWith("send-message")){
                     client.sendMassage(command);
                 }
                 else if(command.equals("get-chats")){
                     client.getChats();
                 }
                 else if(command.startsWith("get-messages")){
                     client.getMessages(command);
                 }
                 else if(command.startsWith("edit-message")){
                     client.editMessage(command);
                 }
                 else if(command.equals("exit")){
                     client.disconnectWorkspace();
                     System.exit(0);
                 }
                 else {
                     throw new Exception("Command not defined");
                 }
             }
             catch (Exception e){
                 e.printStackTrace();
             }
         }
    }
}
