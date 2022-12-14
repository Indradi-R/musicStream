package Server;

import javax.sound.sampled.Clip;

public class Playing extends Thread {
    private Clip clip;
    private long t ;

    /**
     * Constructor: inisialisasi variabel
     */
    public Playing (Clip c, long pos, String sound) {
        this.clip = c;
        t = pos;
    }

    /**
     * Menyetel awal klip ke variabel t dan memulai pemutaran.
     * Metode ini memblokir (clip.drain()) hingga klip selesai diputar..
     */
    @Override
    public void run() {
        clip.setMicrosecondPosition(t);
        clip.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        clip.drain();
        clip.close();
    }

    public long stop(String song) {
        t = clip.getMicrosecondPosition();
        clip.stop();
        return t;
    }

    public boolean selesai() {
        // TODO Auto-generated method stub
        return clip.getMicrosecondPosition() == clip.getMicrosecondLength();
    }

    public void setTempo(int t) {
        this.t = t;
    }
}