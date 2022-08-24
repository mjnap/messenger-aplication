package Host;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class HostConnectionTest {

    static HostConnection hostConnection;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        hostConnection = new HostConnection();
    }

    @Test
    void checkCommandHost() {
        String[] commands = {"","","10000","20000"};
        assertEquals("ERROR At most 1000 ports is allowed", hostConnection.checkCommandHost(commands));

        commands[2] = "9999";
        commands[3] = "10555";
        assertEquals("ERROR Port number must be at least 10000", hostConnection.checkCommandHost(commands));

        commands[2] = "10000";
        commands[3] = "10999";
        assertEquals("OK", hostConnection.checkCommandHost(commands));
        HostConnection.hostList.add(new Host(null, 10000, 10999, null));

        assertEquals("ERROR Port in use by another host", hostConnection.checkCommandHost(commands));

    }
}