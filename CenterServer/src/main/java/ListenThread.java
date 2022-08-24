import ClientThread.ConnectWorkspaceThread;
import ClientThread.CreatWorkspaceThread;
import ClientThread.LoginThread;
import ClientThread.RegisterThread;
import HostThreads.ConnectAgainThread;
import HostThreads.NewHostThread;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;


public class ListenThread implements Runnable{

    ServerSocket serverSocket = new ServerSocket(8000);
    final Executor executor;

    public ListenThread(Executor executor) throws IOException {
        this.executor = executor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());

                String command = input.readUTF();
                if (command.startsWith("create-host")) {
                    executor.execute(new NewHostThread(socket, command));
                } else if (command.startsWith("connect-again")) {
                    executor.execute(new ConnectAgainThread(socket, command));
                } else if (command.startsWith("register")) {
                    executor.execute(new RegisterThread(socket, command));
                } else if (command.startsWith("login")) {
                    executor.execute(new LoginThread(socket, command));
                } else if (command.startsWith("create-workspace")) {
                    executor.execute(new CreatWorkspaceThread(socket, command));
                } else if (command.startsWith("connect-workspace")) {
                    executor.execute(new ConnectWorkspaceThread(socket, command));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
