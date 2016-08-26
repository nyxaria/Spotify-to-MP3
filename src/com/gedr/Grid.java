package com.gedr;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Grid extends JPanel {

    public ArrayList<JCheckBox> checkBoxes = new ArrayList<>();

    public Grid(Playlist[] playlists) {
        setPreferredSize(new Dimension((int) (Main.screen.width * .6) - 24, (playlists.length) * 29));
        setLayout(new GridLayout(0, 1));
        JPanel title = new JPanel(new BorderLayout());
        JLabel downloadLabel = new JLabel("⇩");
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
        JCheckBox test = new JCheckBox();
        downloadLabel.setPreferredSize(test.getPreferredSize());
        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        wrap.add(new JLabel("Playlist"), BorderLayout.CENTER);
        JPanel flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .5), 30));

        flowWrap.add(new JLabel("Author"));
        wrap.add(flowWrap, BorderLayout.EAST);
        title.add(wrap, BorderLayout.CENTER);
        title.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        add(title, "wrap");

        for (Playlist playlist : playlists) {
            JPanel row = new JPanel(new BorderLayout());


//            JPanel previewImages = new JPanel(new GridLayout(2, 2));
//            for (Image image : playlist.images) {
//                previewImages.add(new JLabel(new ImageIcon(image)));
//            }
//            row.add(previewImages, "shrink");
            JCheckBox check = new JCheckBox();
            check.setName(playlist.name);
            checkBoxes.add(check);
            row.add(check, BorderLayout.WEST);
            wrap = new JPanel(new BorderLayout());
            wrap.add(new JLabel(" "), BorderLayout.WEST);
            wrap.add(new JLabel(playlist.name), BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .5), 30));
            flowWrap.add(new JLabel(playlist.owner));
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            add(row, "wrap");
        }
    }

    public Grid(Track[] tracklist) {
        setPreferredSize(new Dimension((int) (Main.screen.width * .6) - 24, (tracklist.length+1) * 29));
        setLayout(new GridLayout(0, 1));
        JCheckBox test = new JCheckBox();
        Dimension prefDim = test.getPreferredSize();
        prefDim.width += 4;
        JPanel title = new JPanel(new BorderLayout());
        JLabel downloadLabel = new JLabel("⇩");
        downloadLabel.setHorizontalAlignment(JLabel.CENTER);
        downloadLabel.setPreferredSize(prefDim);
        title.add(downloadLabel, BorderLayout.WEST);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(new JLabel(" "), BorderLayout.WEST);
        wrap.add(new JLabel("Song"), BorderLayout.CENTER);
        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel flowWrap = new JPanel(new GridLayout());
        flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .6) -40, 30));
        JLabel art = new JLabel("Artist");
        grid.setPreferredSize(new Dimension(100, 30));

        ImageIcon img = new ImageIcon(getClass().getResource("/downloading3.gif"));

        JLabel loadingImg = new JLabel("    ", img, JLabel.CENTER);

        loadingImg.setVisible(false); /***/

        loadingImg.setVerticalTextPosition(JLabel.CENTER);
        loadingImg.setHorizontalTextPosition(JLabel.RIGHT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                img.setImageObserver(loadingImg);
            }
        }).start();
        JPanel Flow = new JPanel(new GridBagLayout());
        Flow.setPreferredSize(new Dimension(33, 30));
        Flow.add(loadingImg);

        grid.add(art);
        flowWrap.add(art);
        flowWrap.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
        JPanel border = new JPanel(new BorderLayout());
        border.add(flowWrap, BorderLayout.CENTER);
        border.add(Flow, BorderLayout.EAST);
        wrap.add(border, BorderLayout.EAST);
        title.add(wrap, BorderLayout.CENTER);
        title.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        add(title, "wrap");

        for (Track track : tracklist) {
            JPanel row = new JPanel(new BorderLayout());

            GraphicLabel check = new GraphicLabel(track);
            check.setBorder(BorderFactory.createLineBorder(Color.gray));
            prefDim.height += 4;
            check.setPreferredSize(prefDim);
            track.gui = check;
            row.add(check, BorderLayout.WEST);
            wrap = new JPanel(new BorderLayout());
            wrap.add(new JLabel(" "), BorderLayout.WEST);
            wrap.add(new JLabel(track.name), BorderLayout.CENTER);
            flowWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
            flowWrap.setPreferredSize(new Dimension((int) (Main.screen.width * .6 * .6), 30));
            String artist = "";
            for (String s : track.artists) {
                artist += s + ", ";
            }
            artist = artist.substring(0, artist.length() - 2);

            flowWrap.add(new JLabel(artist));
            wrap.add(flowWrap, BorderLayout.EAST);
            row.add(wrap, BorderLayout.CENTER);
            add(row, "wrap");
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
