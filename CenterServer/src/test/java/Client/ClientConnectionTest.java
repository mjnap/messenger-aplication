package Client;

import Host.WorkSpace;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ClientConnectionTest {

    static ClientConnection clientConnection;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        clientConnection = new ClientConnection();
        ClientConnection.clientList.addAll(Arrays.asList(new Client("09133573148","anap",""),
                new Client("09217679934", "mjnap","")));
        ClientConnection.workSpaceList.addAll(Arrays.asList(new WorkSpace("company1",null,0)));
    }

    @Test
    void checkCommandRegister() {
        assertTrue(clientConnection.checkCommandRegister("09138598409"));
        assertFalse(clientConnection.checkCommandRegister("09217679934"));
    }

    @Test
    void register() {
        assertEquals("ERROR The client already register", clientConnection.register("register 09217679934 mj"));
        assertEquals("OK",clientConnection.register("register 09138598409 sr81"));
        assertEquals(3,ClientConnection.clientList.size());
    }

    @Test
    void login() {
        assertEquals("OK", clientConnection.login("login 09217679934 mjnap"));
        assertEquals("ERROR phone number or password is wrong", clientConnection.login("login 09217679934 mjna"));
        assertEquals("ERROR phone number or password is wrong", clientConnection.login("login 09217679935 mjnap"));
    }

    @Test
    void checkWorkspace(){
        assertTrue(clientConnection.checkWorkspace("company1_"));
        assertFalse(clientConnection.checkWorkspace("comp@ny"));
        assertFalse(clientConnection.checkWorkspace("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"));
        assertFalse(clientConnection.checkWorkspace("company1"));
    }
}