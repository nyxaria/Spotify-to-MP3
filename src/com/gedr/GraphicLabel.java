package com.gedr;


import javax.swing.*;
import java.awt.*;

public class GraphicLabel extends JLabel {
    DownloadManager.States state = DownloadManager.States.QUEUE;
    Track track;
    String progress, speed;

    public GraphicLabel(Track track) {
        this.track = track;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (state) {
            case QUEUE:
                break;
            case EXTRACTING:
                g2.setColor(new Color(Color.lightGray.getRed() + 10*Integer.parseInt(progress),
                        Color.lightGray.getGreen() + 10*Integer.parseInt(progress),
                        Color.lightGray.getBlue() + 10*Integer.parseInt(progress)));
                g2.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
                break;
            case RETRYING:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
                g2.setColor(new Color(0,191,255));
                //g2.setColor(Color.red);
                double prog = (double) Integer.parseInt(progress);

                if(track.ytDuration == 0) {
                    track.ytDuration = track.duration;
                }

                double percentage = (prog/track.ytDuration);
                if(percentage > 1) {
                    track.ytDuration = track.duration;
                    percentage = 1;
                }
                g2.fillRect(0, getSize().height - 3, (int) (getSize().width*(prog/track.ytDuration)), 3);
                break;
            case EXTRACTING_DONE:
            case DONE:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
                break;
            case ERROR:
                g2.setColor(Color.orange);
                g2.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
                break;
            case DOWNLOADING:
                g2.setColor(new Color(50, Color.green.getGreen(), 50));
                g2.fillRect(0, 0, (int) (getPreferredSize().width * (Double.parseDouble(progress.replaceAll("[^\\d.]", "")) / 100)), getPreferredSize().height);
                g2.setColor(Color.black);

                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
                int y = 3;
                for (String line : speed.split("\n"))
                    g2.drawString(line, 3, y += g2.getFontMetrics().getHeight());
                break;
        }

        g2.dispose();

    }
}
