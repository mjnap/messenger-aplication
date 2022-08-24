package Models;

import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WorkSpaceTest {

    WorkSpace workSpace = new WorkSpace(new Socket(),
            new ArrayList<>(Arrays.asList(new Client("javad", "09217679934"),
                    new Client("ali", "09133573148"))));

    @Test
    void isFirstTime() {
        assertTrue(workSpace.isFirstTime("09138598409"));
        assertFalse(workSpace.isFirstTime("09217679934"));

        workSpace = new WorkSpace(new Socket(), new ArrayList<>());
        assertTrue(workSpace.isFirstTime("09217679934"));
    }

    @Test
    void checkUserName() {
        assertTrue(workSpace.checkUserName("sara"));
        assertFalse(workSpace.checkUserName("javad"));

        workSpace = new WorkSpace(new Socket(), new ArrayList<>());
        assertTrue(workSpace.checkUserName("javad"));
    }

    @Test
    void isUserExist() {
        assertTrue(workSpace.isUserExist("javad"));
        assertFalse(workSpace.isUserExist("sara"));
    }
}