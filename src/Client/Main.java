package Client;
public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        musicStreamInterface ig = new Client.musicStreamInterface();
        ig.showInterface();
        while(ig.isActive()) {
        }
    }
}
