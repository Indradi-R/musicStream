package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MusicServerIO implements Runnable {
    private Socket connection;
    private int id;
    private File directoryIO;
    private OutputStream out;
    private InputStream in;
    private BufferedReader br;

    /**
     * Inisialisasi variabel dan get input output stream.
     */
    public MusicServerIO(Socket connection, int id, File f) {
        this.connection = connection;
        this.id = id;
        this.directoryIO = f;

        if (this.connection != null) {
            try {
                out = this.connection.getOutputStream();
                in = this.connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(in));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Mengirimkan daftar lagu ke client dan menunggu client mengirimkan lagu yang dipilih
     * Setelah Anda mengirim lagu, itu membuka file dan mengirimkannya kepada Anda dalam bentuk blok.
     *
     * Setelah file terkirim, tinggal menunggu request lagu lain.
     */
    public void run() {
        try {
            sendSongList(directoryIO, this.connection);

            String requestedSong = "";

            FileInputStream fis = null;

            requestedSong = receiveSong();
            while (requestedSong != null && requestedSong.compareTo("Exit") != 0) {
                File song = new File(directoryIO.getAbsolutePath() + "\\" + requestedSong + ".wav");
                System.out.println(requestedSong);
                fis = new FileInputStream(song);
                byte buffer[] = new byte[1024];
                int count;
                while ((count = fis.read(buffer)) != -1)
                    out.write(buffer, 0, count);
                out.flush();
                requestedSong = receiveSong();
            }

            if (fis != null)
                fis.close();
            if (out != null)
                out.close();
            this.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Membuat serial daftar lagu yang berisi server, dan mengirimkannya ke klien
     */
    private void sendSongList(File d, Socket client) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(d.list());
            oos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Baca lagu yang diminta oleh client melalui InputStream server.
     * @return String
     */
    private String receiveSong() {
        try {
            return br.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Exit";
        }
    }

    /**
     * Client terputus dari server, dan thread berhenti.
     */
    public void disconnect() {
        try {
            if(connection != null){
                connection.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
