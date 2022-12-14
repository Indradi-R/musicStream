package Server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicServer {

    public static void main(String[] args) {
        ServerSocket server = null;
        File directoryIO = new File("Songs");
        try {
            server = new ServerSocket(7788);
            int idSession = 0;
            while (true) {
                Socket client = server.accept();
                Runnable t = new MusicServerIO(client, idSession, directoryIO);
                new Thread(t).start();
                idSession++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
