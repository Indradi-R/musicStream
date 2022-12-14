package Client;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position.Bias;

public class musicStreamInterface extends JFrame {

    private JPanel contentPane;
    private MusicClient client;
    private JLabel lbSong, lbArtist;
    private JButton btnPlay, btnNext, btnDisconnect;
    private String newSong, oldSong;
    private JList<String> list;
    private JScrollPane scrollPane;
    private boolean randomize;
    private JTextField tfFilter;
    private String[] songs;
    boolean start;

    /**
     * Create the frame.
     */
    public musicStreamInterface() {
        setType(Type.POPUP);
        setBackground(new Color(0, 0, 0));
        setForeground(new Color(0, 0, 0));
        randomize = false;
        setTitle("Music Stream");
        oldSong = "";
        client = new MusicClient("localhost", 7788);

        client.buildConnection();
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 410, 523);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(117, 228, 250));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        DefaultListModel<String> modelo = new DefaultListModel<>();
        songs = client.receiveList();
        for (String s : songs) {
            modelo.addElement(s.split("\\.")[0]);
        }

        scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(30, 111, 315, 240);
        contentPane.add(scrollPane);

        list = new JList<>();
        list.setVisibleRowCount(4);
        scrollPane.setViewportView(list);
        list.setModel(modelo);
        list.setBackground(SystemColor.text);
        list.setFont(new Font("Arial", Font.PLAIN, 20));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);

        list.setBorder(new LineBorder(new Color(0, 0, 0), 2));

//        JLabel lblSongs = new JLabel("Songs");
//        lblSongs.setForeground(new Color(0, 0, 0));
//        lblSongs.setFont(new Font("Times New Roman", Font.BOLD, 25));
//        lblSongs.setBounds(493, 23, 109, 38);
//        lblSongs.setVerticalAlignment(JLabel.CENTER);
//        lblSongs.setHorizontalAlignment(JLabel.CENTER);
//
//        contentPane.add(lblSongs);

        btnPlay = new JButton("Play");
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(btnPlay.getText().compareTo("Play")==0) {
                    played();
                    btnPlay.setText("Pause");
                }else {
                    pause();
                    btnPlay.setText("Play");
                }
            }
        });
        btnPlay.setBounds(138, 355, 97, 25);
        contentPane.add(btnPlay);

        btnNext = new JButton(">>");
        btnNext.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                btnPlay.setText("Pause");
                if (randomize) {
                    nextRandom(songs, modelo);
                } else {
                    nextOrder(songs);
                }
            }
        });

        btnNext.setBounds(247, 355, 97, 25);
        contentPane.add(btnNext);

        JButton button = new JButton("<<");
        start = false;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                btnPlay.setText("Pause");
                if (arg0.getClickCount() == 2) {
                    nextSong(list);
                } else {
                    start = true;
                    backHome(list);
                }
            }
        });

        JRadioButton rdbtnRandom = new JRadioButton("Random");
        rdbtnRandom.setFont(new Font("Tahoma", Font.PLAIN, 12));
        rdbtnRandom.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                randomize = !randomize;
            }
        });
        rdbtnRandom.setToolTipText("Random Order");
        rdbtnRandom.setBounds(257, 400, 73, 25);
        contentPane.add(rdbtnRandom);
        button.setBounds(29, 355, 97, 25);
        contentPane.add(button);

        btnDisconnect = new JButton("Exit");
        btnDisconnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                disconnect();
            }
        });
        btnDisconnect.setBounds(138, 400, 97, 25);
        contentPane.add(btnDisconnect);

        JLabel lblMusicService = new JLabel("Music Stream");
        lblMusicService.setForeground(new Color(0, 0, 0));
        lblMusicService.setFont(new Font("Tahoma", Font.BOLD, 25));
        lblMusicService.setBounds(40, 13, 352, 48);
        lblMusicService.setVerticalAlignment(JLabel.CENTER);
        lblMusicService.setHorizontalAlignment(JLabel.CENTER);
        contentPane.add(lblMusicService);

        tfFilter = createTextField();
        tfFilter.setBounds(100, 76, 240, 22);
        contentPane.add(tfFilter);
        tfFilter.setColumns(10);

        JLabel lblFilter = new JLabel("Filter");
        lblFilter.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblFilter.setBounds(30, 82, 56, 16);
        contentPane.add(lblFilter);

    }

    /**
     * Memainkan lagu selanjutnya
     * @param list2
     */
    protected void nextSong(JList<String> list2) {
        // TODO Auto-generated method stub
        int position = list.getSelectedIndex();
        if (position == 0) {
            position = list.getModel().getSize() - 1;
        } else {
            position = position - 1;
        }
        list.setSelectedIndex(position);
        played();
    }

    /**
     * Melanjutkan lagu yang sedang dimainkan
     * @param list2
     */
    protected void backHome(JList<String> list2) {
        // TODO Auto-generated method stub
        int s = list2.getNextMatch(newSong, 0, Bias.Forward);
        list2.setSelectedIndex(s);
        client.setTempo(0);
        played();
    }

    /**
     * Memainkan lagu berdasarkan list
     * @param songsList
     */
    protected void nextOrder(String[] songsList) {
        // TODO Auto-generated method stub
        int position = list.getSelectedIndex();
        if (position == list.getModel().getSize() - 1) {
            position = 0;
        } else {
            position = position + 1;
        }
        list.setSelectedIndex(position);
        played();
    }

    /**
     * Memainkan lagu acak
     * @param songsList
     * @param model
     */
    protected void nextRandom(String[] songsList, DefaultListModel<String> model) {
        // TODO Auto-generated method stub
        Random generator = new Random();
        int randomIndex = generator.nextInt(songsList.length);
        if (randomIndex == list.getSelectedIndex()) {
            randomIndex++;
        }
        if (randomIndex >= songsList.length) {
            randomIndex = 0;
        }

        list.setSelectedValue(model.getElementAt(randomIndex), true);
        newSong = list.getSelectedValue();
        client.songDemand(newSong);
        client.pauseWAV(oldSong);
        client.playWAV(newSong, oldSong, false);
    }
    /**
     * Memutuskan diri dari server
     */
    protected void disconnect() {
        // TODO Auto-generated method stub
        if (oldSong != null)
            client.pauseWAV(oldSong);
        client.disconnect();
        JOptionPane.showMessageDialog(null, "Music Stream has been Disconnected.", "Music Stream", 1);
        System.exit(0);
    }

    /**
     * Pause
     */
    protected void pause() {
        // TODO Auto-generated method stub
        oldSong = newSong;
        if (oldSong != null) {
            btnPlay.setEnabled(true);
            client.pauseWAV(oldSong);
        }
    }

    public void showInterface() {
        this.setVisible(true);
    }

    /**
     * Memainkan lagu yang dipilih dari JList
     */
    protected void played() {
        if (oldSong != null)
            client.pauseWAV(oldSong);

        newSong = list.getSelectedValue();
        if (newSong != null) {
            client.songDemand(newSong);
            String[] part = newSong.split("-");
//            lbSong.setText("Song:" + part[1]);
//            lbArtist.setText("Artis: " + part[0]);
            if(start) {
                start = false;
                client.playWAV(newSong, oldSong, true);
            }
            else
                client.playWAV(newSong, oldSong, false);
        } else {
            JOptionPane.showMessageDialog(null, "Select Song From List");
        }
    }

    /**
     * Filter lagu berdasarkan string yang diinputkan
     * @param model
     * @param filter
     */
    public void filterModel(DefaultListModel<String> model, String filter) {
        for (String s : songs) {
            String[] part = s.split("\\.");
            if (!s.toLowerCase().contains(filter.toLowerCase())) {
                if (model.contains(part[0])) {
                    model.removeElement(part[0]);
                }
            } else {
                if (!model.contains(part[0])) {
                    model.addElement(part[0]);
                }
            }
        }
    }

    /**
     * Text filed untuk filter
     * @return
     */
    private JTextField createTextField() {

        JTextField field = new JTextField();
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void filter() {
                String filter = field.getText();
                filterModel((DefaultListModel<String>) list.getModel(), filter);
            }
        });
        return field;
    }
}

