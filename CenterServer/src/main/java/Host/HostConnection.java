package Host;

import lombok.NoArgsConstructor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@NoArgsConstructor
public class HostConnection implements Serializable {

    public static List<Host> hostList = new ArrayList<>();
    transient Socket socket;
    transient public DataInputStream input;
    transient public DataOutputStream output;

    public HostConnection(Socket socket) throws IOException {
        this.socket = socket;
        input = new DataInputStream(socket.getInputStream());
        output = new DataOutputStream(socket.getOutputStream());
    }

    public void connectToHost(String command) throws IOException{

        String[] commands = command.split(" ");
        String ip = commands[1];
        int portMin = Integer.parseInt(commands[2]);
        int portMax = Integer.parseInt(commands[3]);

        // check commands and send message to host
        int randPort = 0;
        String messageToHost = checkCommandHost(commands);
        if(messageToHost.startsWith("ERROR")){
            output.writeUTF(messageToHost);
            output.flush();
        }
        else {
            randPort = new Random().nextInt(portMax-portMin+1) + portMin;
            output.writeUTF("OK "+randPort);
            output.flush();
        }

        // if host send "check" central server create a connection
        long code = 3212323321l;
        String messageOfHost = input.readUTF();

        if(messageOfHost.equals("check")) {
            try (Socket tmpSocket = new Socket(ip, randPort);
                 DataOutputStream tmpOutput = new DataOutputStream(tmpSocket.getOutputStream())) {

                tmpOutput.writeUTF("OK " + code);
                tmpOutput.flush();
            }

            // if the code has sent from host is true, the host add the list
            long codeOfHost = input.readLong();
            if (code == codeOfHost) {
                hostList.add(new Host(ip, portMin, portMax, socket));
                output.writeUTF("OK");
                output.flush();
            } else {
                output.writeUTF("ERROR Invalid code");
                output.flush();
            }
        }
    }

    public void connectAgain(String command){
        String ip = command.split(" ")[1];

        Host host = hostList.stream()
                        .filter(host1 -> host1.getIp().equals(ip))
                        .collect(Collectors.toList()).get(0);
        host.socket = socket;
        for(int i=0; i<hostList.size(); i++)
            if(hostList.get(i).getIp().equals(ip))
                hostList.set(i, host);
    }

    String checkCommandHost(String[] commands){
        int portMin = Integer.parseInt(commands[2]);
        int portMax = Integer.parseInt(commands[3]);

        boolean checkValid = hostList.stream()
                .anyMatch(port -> (port.getPortMin() <= portMin && port.getPortMax() >= portMin)
                                || (port.getPortMin() <= portMax && port.getPortMax() >= portMax));

        if(checkValid) {
            return "ERROR Port in use by another host";
        }
        else if(portMax-portMin+1 > 1_000){
            return "ERROR At most 1000 ports is allowed";
        }
        else if(portMin < 10_000){
            return "ERROR Port number must be at least 10000";
        }

        return "OK";
    }

}
