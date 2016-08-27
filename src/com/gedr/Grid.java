package com.gedr;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel {

    public JPanel row;
    public ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

    //Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
    public Grid(Playlist[] playlists) {
        setPreferredSize(new Dimension((int) (Main.screen.width * .6) - 24, (playlists.length) * 29));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setBackground(new Color(28, 28, 28));
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        JLabel downloadLabel = new JLabel(" ");
        title.setOpaque(false);
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
        JCheckBox test = new JCheckBox();
        Dimension prefDim = test.getPreferredSize();
        prefDim.width += 28;
        prefDim.height += 30;
        downloadLabel.setPreferredSize(prefDim);
        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        JLabel playlistLabel = new JLabel("PLAYLIST");
        playlistLabel.setFont(Main.fontNormal.deriveFont(12f));
        playlistLabel.setForeground(Color.gray);
        wrap.add(playlistLabel, BorderLayout.CENTER);
        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flowWrap.setOpaque(false);
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        title.add(wrap, BorderLayout.CENTER);
        add(title);
        add(new Separator(false));
        int i = 1;
        for (Playlist playlist : playlists) {

            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));

            row.setOpaque(false);
//            JPanel previewImages = new JPanel(new GridLayout(2, 2));
//            for (Image image : playlist.images) {
//                previewImages.add(new JLabel(new ImageIcon(image)));
//            }
//            row.add(previewImages, "shrink");
            JCheckBox check = new JCheckBox();
            check.setPreferredSize(prefDim);
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
            playlistLabel.setFont(Main.fontNormal.deriveFont(13f));
            playlistLabel.setForeground(Color.white);
            wrap.add(playlistLabel, BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setOpaque(false);
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            add(row);
            if(i != playlists.length)
                add(new Separator(false));
            i++;
            if(i==2)
            this.row = row;
        }
    }

    public Grid(Track[] tracklist) {
        setPreferredSize(new Dimension((int) (Main.screen.width * .6) - 24, (tracklist.length+1) * (29+8)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(true);
        setBackground(new Color(28, 28, 28));
        JCheckBox test = new JCheckBox();
        Dimension prefDim = test.getPreferredSize();
        prefDim.width += 28;
        prefDim.height +=20;
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        JLabel downloadLabel = new JLabel(" ");
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
        downloadLabel.setPreferredSize(prefDim);
        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        JLabel songLabel = new JLabel("SONG");
        songLabel.setFont(Main.fontNormal.deriveFont(12f));
        downloadLabel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        songLabel.setForeground(Color.gray);
        wrap.add(songLabel, BorderLayout.CENTER);
        wrap.setOpaque(false);
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        grid.setOpaque(false);
        JPanel flowWrap = new JPanel(new GridLayout());
        flowWrap.setOpaque(false);
        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6*.6)-6, 40));
        JLabel art = new JLabel("ARTIST");
        art.setFont(Main.fontNormal.deriveFont(12f));
        art.setForeground(Color.gray);

        //grid.setPreferredSize(new Dimension(100, 40));


        JPanel Flow = new JPanel(new GridBagLayout());
        //Flow.setPreferredSize(new Dimension(70, 40));
        //Flow.add(loadingImg);
        Flow.setOpaque(false);

        grid.add(art);
        flowWrap.add(art);
        flowWrap.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
        JPanel border = new JPanel(new BorderLayout());
        border.add(flowWrap, BorderLayout.CENTER);
        //border.add(Flow, BorderLayout.EAST);
        border.setOpaque(false);
        wrap.add(border, BorderLayout.EAST);
        title.add(wrap, BorderLayout.CENTER);
        title.setPreferredSize(new Dimension(title.getWidth(), 40));
        add(title, "wrap");
        add(new Separator(false));
        int i = 1;
        for (Track track : tracklist) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));

            row.setOpaque(false);
            GraphicLabel check = new GraphicLabel(track);
            check.setBorder(BorderFactory.createLineBorder(Color.gray));
            prefDim.height += 4;
            check.setPreferredSize(prefDim);
            track.gui = check;
            row.add(check, BorderLayout.WEST);
            wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.add(new JLabel(" "), BorderLayout.WEST);
            songLabel = new JLabel(track.name);
            songLabel.setFont(Main.fontNormal.deriveFont(13f));
            songLabel.setForeground(new Color(240,240,240));
            wrap.add(songLabel, BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setOpaque(false);
            flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .6), 30));
            String artist = "";
            for (String s : track.artists) {
                artist += s + ", ";
            }
            artist = artist.substring(0, artist.length() - 2);
            art = new JLabel(artist);
            art.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .6),(int) art.getPreferredSize().getHeight()));

            art.setForeground(new Color(240,240,240));
            art.setFont(Main.fontNormal.deriveFont(13f));
            flowWrap.add(art);
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            row.setPreferredSize(new Dimension(title.getWidth(), 40));

            add(row, "wrap");
            if(i != tracklist.length)
                add(new Separator(false));
            i++;

        }
    }
}








//    public Grid(Track[] tracklist) {
//        setPreferredSize(new Dimension((int) (Main.screen.width * .6) - 24, (tracklist.length) * 30));
//        setLayout(new GridLayout(0, 1));
//        JPanel title = new JPanel(new BorderLayout());
//        JLabel downloadLabel = new JLabel("⇩");
//        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
//        JCheckBox test = new JCheckBox();
//        Dimension prefDim = test.getPreferredSize();
//        Track.MAX_WIDTH = prefDim.width;
//        downloadLabel.setPreferredSize(prefDim);
//        title.add(downloadLabel, BorderLayout.WEST);
//        JPanel wrap = new JPanel(new BorderLayout());
//        JPanel smallWrap = new JPanel(new BorderLayout());
//        smallWrap.add(new JLabel(" "), BorderLayout.WEST);
//        smallWrap.add(new JLabel("Song"), BorderLayout.CENTER);
//        wrap.add(smallWrap, BorderLayout.WEST);
//        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JLabel art = new JLabel("Artist");
//        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .7), 28));
//
//        art.setHorizontalAlignment(JLabel.CENTER);
//        flowWrap.add(art);
////        wrap.add(flowWrap, BorderLayout.EAST);
//        JPanel borderWrap = new JPanel(new BorderLayout());
//        borderWrap.add(flowWrap, BorderLayout.EAST);
//        wrap.add(borderWrap, BorderLayout.CENTER);
//        title.add(wrap, BorderLayout.CENTER);
//        title.setBorder(BorderFactory.createLineBorder(Color.lightGray));
//        add(title);
//
//        for (Track track : tracklist) {
//            JPanel row = new JPanel(new BorderLayout());
//            row.setPreferredSize(new Dimension(-1, 30));
//
////            JPanel previewImages = new JPanel(new GridLayout(2, 2));
////            for (Image image : playlist.images) {
////                previewImages.add(new JLabel(new ImageIcon(image)));
////            }
////            row.add(previewImages, "shrink");
//            JLabel check = new JLabel();
//            check.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 8));
//            check.setName(track.name);
//            track.gui = check;
//
//            check.setPreferredSize(prefDim);
//            row.add(check, BorderLayout.WEST);
//            wrap = new JPanel(new BorderLayout());
//            smallWrap = new JPanel(new BorderLayout());
//            smallWrap.add(new JLabel(" "), BorderLayout.WEST);
//            smallWrap.add(new JLabel(track.name), BorderLayout.CENTER);
//            wrap.add(smallWrap, BorderLayout.WEST);
//            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
//            flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .5), 40));
//            String artist = "";
//            for (String s : track.artists) {
//                artist += s + ", ";
//            }
//            artist = artist.substring(0, artist.length() - 2);
//            flowWrap.add(new JLabel(artist));
//            borderWrap = new JPanel(new BorderLayout());
//            borderWrap.add(new JLabel(artist), BorderLayout.EAST);
//            wrap.add(borderWrap, BorderLayout.CENTER);
//            row.add(wrap, BorderLayout.CENTER);
//            add(row, "wrap");
//        }
//    }
//}
