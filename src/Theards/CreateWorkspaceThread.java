package Theards;

import main.Host;

import java.io.IOException;

public class CreateWorkspaceThread extends Host implements Runnable {

    private String command;

    public CreateWorkspaceThread(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        try {
            createWorkspace(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
