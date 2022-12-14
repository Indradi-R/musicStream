package Client;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import Server.Playing;

public class MusicClient {
    private OutputStream os;
    private InputStream is;
    private Socket client;
    private int port;
    private String ipService;
    private Clip clip;
    private Playing inOut = null;
    private long pausePosition;


    /**
     * Membuat class MusicClient dan inisialisasi variabel.
     * @param ipServer
     * @param port
     */
    public MusicClient(String ipServer, int port) {
        this.port = port;
        this.ipService = ipServer;
    }

    /**
     * Membuat class buildConnection dengan ipService menggunakan input output stream
     *
     * @exception IOException
     */
    public boolean buildConnection() {
        try {
            client = new Socket(ipService, port);
            os = client.getOutputStream();
            is = client.getInputStream();
            pausePosition = 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return client !=null;
    }

    /**
     * Berfungsi untuk mengembalikan daftar lagu yang disimpan di server
     *
     * @exception IOException, ClassNotFoundException
     */
    public String[] receiveList() {
        try {
            ObjectInputStream ois = new ObjectInputStream(is);
            String[] list = (String[]) ois.readObject();
            return list;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * read data lagu yang dipilih dan mengirimkan data lagu ke server
     */
    public void songDemand(String requestedSong) {
        PrintWriter pw = new PrintWriter(os);
        pw.println(requestedSong);
        pw.flush();
    }

    /**
     * Berfungsi untuk menutup sambungan ke server
     *
     * @exception IOException
     */
    public void disconnect() {
        if (client != null)
            try {
                System.out.println("You've been disconnected, see you soon.");
                client.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }


    /**
     * Membuat thread untuk memutar lagu dengan parameter lagu dan memberikan kondisi,
     *Jika lagu yang diputar berbeda dengan yang ingin diputar, maka posisi untuk memutar adalah 0. Jika parameter start true, artinya lagu yang ingin diputar dari awal.
     * @throws UnsupportedAudioFileException, IOException, LineUnavailableException
     */
    public void playWAV(String sound, String prevSong, boolean start) {
        is = new BufferedInputStream(is);
        AudioInputStream ais;
        try {
            System.out.println(is);
            System.out.println(sound);
            System.out.println(start);
            System.out.println(prevSong);
            System.out.println(AudioSystem.getAudioFileFormat(is));
            ais = AudioSystem.getAudioInputStream(is);
            this.clip = AudioSystem.getClip();
            clip.open(ais);
            if((prevSong!=null && sound.compareTo(prevSong)!=0)) {
                pausePosition =0;
            }

            if(start) {
                pausePosition = 0;
            }
            inOut = new Playing(clip, pausePosition, sound);
            inOut.start();

        } catch (UnsupportedAudioFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Fungsi untuk jeda lagu
     *
     * @param song
     */
    public void pauseWAV(String song) {
        if(inOut != null) {
//            inOut.stop(song);
            pausePosition = inOut.stop(song);
        }
    }

    /**
     * Cek kondisi bila lagu telah selesai diputar
     *
     * @return true si ha acabado, y falso en caso contrario
     */
    public boolean finish() {
        return inOut.selesai();
    }

    /**
     * Mengatur posisi audio dari thread pemutar.
     *
     * @param t value from where you want to play the song
     */
    public void setTempo(int t)
    {
        inOut.setTempo(t);
    }
}
