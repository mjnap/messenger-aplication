package Models;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.DataOutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class HostTest {

    @SneakyThrows
    @Test
    void connection() {
        String createHostCommand = "create-host 127.0.0.1 1000 1500";
        String ip = createHostCommand.split(" ")[1];

        Host host = new Host(ip);
        try {
            host.connection(createHostCommand);
            fail();
        }catch (Exception e){
            assertEquals("ERROR Port number must be at least 10000", e.getMessage());
        }

        createHostCommand = "create-host 127.0.0.1 10000 20000";
        host = new Host(ip);
        try {
            host.connection(createHostCommand);
            fail();
        }catch (Exception e){
            assertEquals("ERROR At most 1000 ports is allowed", e.getMessage());
        }

        createHostCommand = "create-host 127.0.0.1 10000 10999";
        host = new Host(ip);
        try {
            host.connection(createHostCommand);
        }catch (Exception e){
            fail();
        }
    }

    @SneakyThrows
    @Test
    void createWorkspace() {
        String createHostCommand = "create-host 127.0.0.1 10000 10999";
        String ip = createHostCommand.split(" ")[1];
        Host host = new Host(ip);
        host.connection(createHostCommand);

        assertEquals(0, host.getWorkSpaceList().size());
        host.createWorkspace("create-workspace 10142 1001");
        assertEquals(1, host.getWorkSpaceList().size());
    }
}