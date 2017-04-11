package com.gedr;

import com.gedr.Managers.DownloadManager;
import com.gedr.Managers.Global;
import com.gedr.Managers.JarHandler;
import com.gedr.Managers.SpotifySession;
import com.gedr.Modules.Playlist;
import com.gedr.UI.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static boolean settingUp = true;

    public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    static SpotifySession currentSession;
    private ArrayList<Playlist> playlists;

    public static JFrame frame;
    static JPanel formPane;
    private JButton downloadButton;

    static Grid grid;
    private HintTextField idField;
    public static LoadingLabel loadingImg;

    public static void main(String[] args) {
        new Main();
    }

    private MouseListener hoverListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {((JComponent) e.getSource()).setForeground(Color.white);}

        @Override
        public void mouseExited(MouseEvent e) {
            ((JComponent) e.getSource()).setForeground(Color.gray);
        }
    };

    private ActionListener animationEvent = e -> new Thread(() -> {
        if(idField.getText().equals("") || idField.loading) return;
        idField.loading = true;
        idField.repaint();
        idField.revalidate();
        currentSession = new SpotifySession(idField.getText());
        playlists = currentSession.getPlaylists();
        if(!playlists.isEmpty()) {
            setupPlaylistGrid(playlists.toArray(new Playlist[playlists.size()]));
        } else {
            idField.red = Color.red;
            idField.startAnimation();
        }
        idField.loading = false;
        idField.running = false;
        idField.thread = null;
    }).start();

    public Main() {

        try {
            initialize();
        } catch(Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Spotify to MP3");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //handle that later
        frame.setSize(864, 540);
        frame.setLocation((int) screen.getWidth() / 2 - 864 / 2, (int) screen.getHeight() / 2 - 540 / 2);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Global.darkerGray);

        JPanel center = new JPanel(new GridBagLayout());

        formPane = new JPanel(new BorderLayout());
        idField = new HintTextField("Spotify username");
        idField.setColumns(16);
        JButton loginButton = new JButton("Sign in");
        loginButton.setFont(Global.fontBold.deriveFont(15f));
        loginButton.setPreferredSize(new Dimension(60, loginButton.getPreferredSize().height));
        idField.setPreferredSize(new Dimension(loginButton.getPreferredSize().width, loginButton.getPreferredSize().height));

        loginButton.setForeground(Color.gray);
        loginButton.addMouseListener(hoverListener);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        loginButton.setOpaque(false);

        idField.addActionListener(animationEvent);
        loginButton.addActionListener(animationEvent);

        Image img = null;
        try {
            img = ImageIO.read(getClass().getResourceAsStream("/spotify_icon_128.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }

        JLabel icon = new JLabel(new ImageIcon(img));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        formPane.add(BorderLayout.NORTH, icon);
        formPane.add(BorderLayout.CENTER, idField);
        formPane.add(BorderLayout.SOUTH, loginButton);
        formPane.setOpaque(false);
        center.setOpaque(false);

        center.add(formPane);

        mainPanel.add(BorderLayout.CENTER, center);
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
        idField.transferFocus();
    }

    public void setupPlaylistGrid(Playlist[] playlists) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Global.darkerGray);

        grid = new Grid(playlists);
        JPanel wrapp = new JPanel();
        wrapp.setLayout(new BoxLayout(wrapp, BoxLayout.Y_AXIS));
        wrapp.setPreferredSize(new Dimension(Main.frame.getSize().width, (29 + 9) * (playlists.length + 1)));
        wrapp.setOpaque(true);

        wrapp.add(grid);
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.setOpaque(false);
        flow.setPreferredSize(new Dimension(Main.frame.getSize().width, (29 + 9) * (playlists.length + 1)));
        flow.add(wrapp);
        JScrollPane scroll = setupScrollPane(flow);

        mainPanel.add(BorderLayout.CENTER, scroll);
        loadingImg = new LoadingLabel(4);
        loadingImg.setPreferredSize(new Dimension(42, 42));
        JPanel gridBag = new JPanel(new GridBagLayout());
        gridBag.add(loadingImg);
        gridBag.setOpaque(false);
        gridBag.setPreferredSize(new Dimension(70, 50));

        JPanel divider = new JPanel(new BorderLayout());
        divider.add(gridBag, BorderLayout.CENTER);
        divider.setOpaque(false);

        downloadButton = new JButton("Download Selected");
        downloadButton.setFont(Global.fontNormal.deriveFont(14f));
        downloadButton.setForeground(Color.gray);
        downloadButton.addMouseListener(hoverListener);
        downloadButton.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 5));
        downloadButton.setOpaque(false);

        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flowWrap.setOpaque(false);
        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 - loadingImg.getPreferredSize().getWidth()), 50));
        JPanel gridWrap = new JPanel(new GridBagLayout());
        gridWrap.add(downloadButton);
        gridWrap.setPreferredSize(new Dimension(downloadButton.getPreferredSize().width, 50));
        gridWrap.setOpaque(false);
        flowWrap.add(gridWrap);
        divider.add(gridBag, BorderLayout.WEST);
        divider.add(flowWrap, BorderLayout.EAST);
        divider.setOpaque(true);
        divider.setBackground(Global.darkGray);
        mainPanel.add(BorderLayout.NORTH, divider);
        //frame.removeAll();
        frame.setContentPane(mainPanel);

        if(!settingUp) loadingImg.setVisible(false);
        else loadingImg.setVisible(true);

        downloadButton.addActionListener(startDownloadProcess);
    }

    ActionListener startDownloadProcess = e -> {
        if(settingUp) return;

        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        FileDialog fc = new FileDialog(frame, "Choose directory to download playlist into");
        fc.setVisible(true);
        String fn = fc.getFile();
        if(fn == null) return;
        else DownloadManager.output = new File(fc.getDirectory() + "/" + fn);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");

        loadingImg.setVisible(true);

        if(DownloadManager.output != null) {
            DownloadManager.temp = new File(DownloadManager.output.getAbsolutePath() + "/_temp");
            DownloadManager.temp.mkdirs();
            DownloadManager.temp.deleteOnExit();
        } else {
            return;
        }

        ArrayList<Playlist> toDownload = new ArrayList<>();
        grid.checkBoxes.stream().filter(box -> box.checked).forEach(box -> {
            playlists.stream().filter(p -> p.name.equals(box.getName()) && p.total != 0).forEach(p -> {
                toDownload.add(p);
                currentSession.getTracks(p);
            });
        });

        if(!toDownload.isEmpty()) setupTracklist(toDownload);
    };

    public void setupTracklist(ArrayList<Playlist> toDownload) {
        int height = 39; //adjust for dividers

        JPanel tracklistWrap = new JPanel();
        tracklistWrap.setLayout(new BoxLayout(tracklistWrap, BoxLayout.Y_AXIS));
        int size = 0;
        int index = 0;
        for(Playlist playlist : toDownload) {
            if(playlist.tracks.length != 0) {
                Grid grid = new Grid(playlist.tracks, playlist.name);
                System.out.println(playlist.name + " - " + playlist.tracks.length);
                grid.setPreferredSize(new Dimension(Main.frame.getSize().width, height * (playlist.tracks.length + 1) + 5 * playlist.tracks.length + 1));
                tracklistWrap.add(grid);
                if(index++ + 1 != toDownload.size()) {
                    Separator sep = new Separator(true);
                    tracklistWrap.add(Box.createVerticalStrut(12));
                    sep.setMinimumSize(new Dimension(Main.frame.getWidth() - 20, 20));
                    tracklistWrap.add(sep);
                    tracklistWrap.add(Box.createVerticalStrut(9));

                }
                size += playlist.tracks.length + 1;
            } else {
                toDownload.remove(playlist);
                toDownload.trimToSize();
            }
        }
        tracklistWrap.setPreferredSize(new Dimension(Main.frame.getSize().width, (index) * 28 + (height) * (size) - (toDownload.size() == 1 ? 36 : 0)));

        tracklistWrap.setOpaque(false);
        JPanel flow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flow.setOpaque(false);

        flow.add(tracklistWrap);
        JScrollPane scroll = setupScrollPane(flow);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, scroll);
        mainPanel.setOpaque(false);
        //frame.removeAll();
        frame.setContentPane(mainPanel);

        new Thread(() -> {
            new DownloadManager(toDownload.toArray(new Playlist[toDownload.size()]));
        }).start();

    }

    public JScrollPane setupScrollPane(JPanel child) {
        JScrollPane scroll = new JScrollPane(child, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(Global.darkerGray);
        scroll.getVerticalScrollBar().setUnitIncrement(19);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new SpotifyScroll());
        return scroll;
    }

    private void initialize() throws Exception {
        Global.fontNormal = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Light.otf"));
        Global.fontThin = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Hairline.otf"));
        Global.fontBold = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Montserrat-Regular.otf"));

        //set up paths to exes, extract from jar if exported
        new JarHandler().run();
    }
}
