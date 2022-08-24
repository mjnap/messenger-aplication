package Theards;

import lombok.Setter;
import Models.Client;
import Models.Host;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class WorkspaceThread implements Runnable, Serializable {

    private int port;
    @Setter
    private transient Executor executor;
    private List<Client> clientList = new ArrayList<>();
    @Setter
    private Host host;

    public WorkspaceThread(int port, Executor executor, Host host) {
        this.port = port;
        this.executor = executor;
        this.host = host;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true){
                Socket socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());

                String command = input.readUTF();
                if(command.startsWith("connect")){
                    executor.execute(new ConnectToClientThread(socket, clientList, command, host));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
