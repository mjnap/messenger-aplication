package Host;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Host implements Serializable {

    @Getter
    private String ip;
    @Getter
    private int portMin;
    @Getter
    private int portMax;
    public List<Integer> portList;
    public transient Socket socket;

    public Host(String ip, int portMin, int portMax, Socket socket) {
        this.ip = ip;
        this.portMin = portMin;
        this.portMax = portMax;
        this.socket = socket;
        portList = new ArrayList<>();

        for(int i=portMin; i<=portMax; i++)
            portList.add(i);
    }
}
