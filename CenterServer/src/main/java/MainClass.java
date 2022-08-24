import java.io.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class MainClass {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Executor executor = Executors.newCachedThreadPool();

        // if the data file was exist, it loads it
        new LoadData();

        // thread for listen
        new Thread(new ListenThread(executor)).start();

        // thread for shutdown
        new Thread(new SaveThread()).start();
    }
}
