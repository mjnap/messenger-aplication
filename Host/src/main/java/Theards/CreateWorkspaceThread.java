package Theards;

import lombok.AllArgsConstructor;
import Models.Host;

import java.io.IOException;

@AllArgsConstructor
public class CreateWorkspaceThread implements Runnable {

    private String command;
    private Host host;

    @Override
    public void run() {
        try {
            host.createWorkspace(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
