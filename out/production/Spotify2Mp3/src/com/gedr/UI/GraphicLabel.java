package com.gedr.UI;

import com.gedr.Managers.DownloadManager;
import com.gedr.Managers.Global;
import com.gedr.Modules.Track;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class GraphicLabel extends JLabel {

    public DownloadManager.States state = DownloadManager.States.QUEUE;
    Track track;

    public String progress;
    public String speed;
    public String out;

    boolean done;

    public GraphicLabel(Track track) {
        this.track = track;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        Global.prettify(g2);

        int xOff = getWidth()/2 - getHeight()/2;
        Ellipse2D clipArea = new Ellipse2D.Float(xOff+1,1,getHeight()-2,getHeight()-2);

        g2.setClip(clipArea);

        switch (state) {
            case QUEUE:
                break;
            case EXTRACTING:
                g2.setColor(new Color(Global.darkGray.getRed() + 40 - 10*Integer.parseInt(progress),
                        Global.darkGray.getGreen() + 40 - 10*Integer.parseInt(progress),
                        Global.darkGray.getBlue() + 40 - 10*Integer.parseInt(progress)));
                g2.fillRect(xOff, 0, getHeight(), getPreferredSize().height);
                break;
            case RETRYING:
                g2.setColor(Global.darkGray);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                g2.setColor(Global.spotifyGreen);
                double prog = (double) Integer.parseInt(progress);

                if(track.ytDuration == 0) {
                    track.ytDuration = track.duration;
                }

                if(prog/track.ytDuration > 1) { //percentage
                    track.ytDuration = track.duration;
                }
                g2.fillRect(xOff, getSize().height - (int) (getHeight()*(prog/track.ytDuration)), getHeight(), (int) (getHeight()*(prog/track.ytDuration)));
                break;
            case EXTRACTING_DONE:
            case DONE:
                g2.setColor(Global.spotifyGreen);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                done = true;
                break;
            case ERROR:
                g2.setColor(Color.orange);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                break;
            case DOWNLOADING:
                g2.setColor(Global.darkGray);
                g2.fillRect(xOff, 0, (int) (getHeight() * (Double.parseDouble(progress.replaceAll("[^\\d.]", "")) / 100)), getHeight());
                break;
        }
        g2.setClip(0,0,getWidth(),getHeight());

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.darkGray);
        g2.draw(clipArea);

        g2.dispose();

    }
}
