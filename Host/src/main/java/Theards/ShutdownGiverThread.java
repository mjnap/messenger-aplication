package Theards;

import DataRepository.SaveData;
import lombok.AllArgsConstructor;
import Models.Host;

import java.io.IOException;
import java.util.Scanner;

@AllArgsConstructor
public class ShutdownGiverThread extends SaveData implements Runnable{

    Scanner scanner;
    Host host;

    @Override
    public void run() {
        String command = "";
        while (!command.equals("shutdown")){
            command = scanner.next();
            if(command.equals("shutdown")){
                try {
                    save(host);
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
