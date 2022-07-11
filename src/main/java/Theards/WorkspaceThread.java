package Theards;


import other.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class WorkspaceThread implements Runnable{

    private int port;
    private Executor executor;
    private List<Client> clientList = new ArrayList<>();

    public WorkspaceThread(int port, Executor executor) {
        this.port = port;
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true){
                Socket socket = serverSocket.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                String command = input.readUTF();
                if(command.startsWith("connect")){
                    executor.execute(new ConnectToClientThread(socket, clientList, command));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
