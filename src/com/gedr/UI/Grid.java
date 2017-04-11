package com.gedr.UI;

import com.gedr.Main;
import com.gedr.Managers.Global;
import com.gedr.Modules.Playlist;
import com.gedr.Modules.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Grid extends JPanel {

    public JPanel row;
    private JPanel selected;
    private JPanel hover;

    public ArrayList<SpotifyCheckBox> checkBoxes = new ArrayList<>();
    Playlist[] playlists;

    public Grid(Playlist[] playlists) {
        this.playlists = playlists;
        setPreferredSize(new Dimension((int) (Main.frame.getWidth()) - 24, (playlists.length) * 29));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Global.darkerGray);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        SpotifyCheckBox downloadLabel = new SpotifyCheckBox(" ");
        downloadLabel.empty = true;
        downloadLabel.setPreferredSize(new Dimension(50, 38));

        title.setOpaque(false);
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);

        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        JLabel playlistLabel = new JLabel("PLAYLIST");
        playlistLabel.setFont(Global.fontNormal.deriveFont(12f));
        playlistLabel.setForeground(Color.gray);
        wrap.add(playlistLabel, BorderLayout.CENTER);
        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flowWrap.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(16, 0, 6, 0));

        title.add(wrap, BorderLayout.CENTER);
        add(title);
        add(new Separator(false));
        add(Box.createVerticalStrut(1));
        int i = 1;
        for(Playlist playlist : playlists) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            row.setOpaque(false);
            SpotifyCheckBox check = new SpotifyCheckBox("FDFDF");
            check.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            check.setPreferredSize(new Dimension(50, 38));
            check.setName(playlist.name);
            checkBoxes.add(check);
            JPanel gridbagWrap = new JPanel(new GridBagLayout());
            gridbagWrap.add(check);
            gridbagWrap.setOpaque(false);
            row.add(gridbagWrap, BorderLayout.WEST);
            wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(new JLabel(" "), BorderLayout.WEST);
            playlistLabel = new JLabel(playlist.name);
            playlistLabel.setFont(Global.fontNormal.deriveFont(13f));
            playlistLabel.setForeground(Color.white);
            wrap.add(playlistLabel, BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setOpaque(false);
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            add(row);
            if(i != playlists.length) {
                add(new Separator(false));
                add(Box.createVerticalStrut(1));
            }
            i++;
            if(i == 2)
                this.row = row;
        }
    }

    public Grid(Track[] tracklist, String name) {

        setPreferredSize(new Dimension( Main.frame.getWidth(), (tracklist.length + 1) * (29 + 8)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Global.darkerGray);
        JCheckBox test = new JCheckBox();
        Dimension prefDim = test.getPreferredSize();
        prefDim.width += 28;
        prefDim.height += 20;
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        JLabel downloadLabel = new JLabel(" ");
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
        downloadLabel.setPreferredSize(prefDim);
        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        JLabel songLabel = new JLabel("SONG");
        songLabel.setFont(Global.fontNormal.deriveFont(12f));

        songLabel.setForeground(Color.gray);
        wrap.add(songLabel, BorderLayout.CENTER);
        wrap.setOpaque(false);
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grid.setOpaque(false);
        JPanel flowWrap = new JPanel(new GridLayout());
        flowWrap.setOpaque(false);
        JLabel art = new JLabel("ARTIST");
        art.setForeground(Color.gray);
        art.setFont(Global.fontNormal.deriveFont(12f));

        JPanel Flow = new JPanel(new GridBagLayout());
        JLabel playlistLabel = new JLabel(name +"      ");
        playlistLabel.setFont(Global.fontBold.deriveFont(15f));
        playlistLabel.setForeground(Color.lightGray);
        Flow.setOpaque(false);
        Flow.add(playlistLabel);
        flowWrap.setPreferredSize(new Dimension((int) (Main.frame.getWidth() * .6) - playlistLabel.getPreferredSize().width - 6, 40));

        grid.add(art);
        flowWrap.add(art);
        flowWrap.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
        JPanel border = new JPanel(new BorderLayout());
        border.add(flowWrap, BorderLayout.WEST);
        border.add(playlistLabel, BorderLayout.EAST);
        border.setOpaque(false);
        wrap.add(border, BorderLayout.EAST);
        title.add(wrap, BorderLayout.CENTER);
        title.setMinimumSize(new Dimension(Main.frame.getWidth()-10, 30));
        add(Box.createVerticalStrut(4));
        add(title);
        add(new Separator(false));
        int i = 1;
        for(Track track : tracklist) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            row.setOpaque(true);
            row.setBackground(new Color(28,28,28));
            GraphicLabel check = new GraphicLabel(track);

            MouseListener launchMP3Listener = new MouseListener() {
                long last = System.currentTimeMillis();
                @Override public void mouseClicked(MouseEvent e) {}

                @Override
                public void mousePressed(MouseEvent e) {
                    if(System.currentTimeMillis() - last < 250) {
                        if(!check.done) return;
                        if(new File(check.out).exists()) if(Desktop.isDesktopSupported()) try {
                            Desktop.getDesktop().open(new File(check.out));
                        } catch(IOException e1) {
                            e1.printStackTrace();
                        }
                        if(selected != null) selected.setBackground(Global.darkerGray);
                        selected = row;
                        selected.setBackground(new Color(48, 48, 48));
                    } else {
                        last = System.currentTimeMillis();
                    }
                }

                @Override public void mouseReleased(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = row;
                    hover.setBackground(new Color(38,38,38));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if(!hover.equals(selected)) hover.setBackground(Global.darkerGray);
                    else hover.setBackground(new Color(48, 48, 48));

                }
            };

            prefDim.height += 10;
            check.setPreferredSize(prefDim);
            track.gui = check;
            row.add(check, BorderLayout.WEST);
            wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(new JLabel(" "), BorderLayout.WEST);
            songLabel = new JLabel(track.name);
            songLabel.setFont(Global.fontNormal.deriveFont(13f));
            songLabel.setForeground(new Color(240, 240, 240));
            wrap.add(songLabel, BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setOpaque(false);
            flowWrap.setPreferredSize(new Dimension((int) (Main.frame.getWidth() * .6), 30));
            String artist = "";
            for(String s : track.artists) {
                artist += s + ", ";
            }
            artist = artist.substring(0, artist.length() - 2);
            art = new JLabel(artist);
            art.setPreferredSize(new Dimension((int) (Main.frame.getWidth() * .6), (int) art.getPreferredSize().getHeight()));

            art.setForeground(new Color(240, 240, 240));
            art.setFont(Global.fontNormal.deriveFont(13f));

            row.addMouseListener(launchMP3Listener);
            flowWrap.add(art);
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            row.setMinimumSize(new Dimension(Main.frame.getWidth()-10, 38));
            add(row, "wrap");
            if(i != tracklist.length)
                add(new Separator(false));
            i++;

        }
    }
}