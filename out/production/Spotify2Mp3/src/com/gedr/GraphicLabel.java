package com.gedr;


import javafx.scene.shape.Ellipse;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class GraphicLabel extends JLabel {
    DownloadManager.States state = DownloadManager.States.QUEUE;
    Track track;
    String progress, speed;
    boolean done;
    public String out;

    public GraphicLabel(Track track) {
        this.track = track;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color green = new Color(9,166,73);
        Color background = new Color(38,38,38);

//        BufferedImage mask = new BufferedImage(getHeight(), getHeight(), BufferedImage.TYPE_INT_ARGB);
//        Graphics2D g2b = mask.createGraphics();
//        g2b.setColor(background);
//        g2b.fillRect(0,0,mask.getWidth(),mask.getHeight());

        int xOff = getWidth()/2 - getHeight()/2;
        Ellipse2D clipArea = new Ellipse2D.Float(xOff+1,1,getHeight()-2,getHeight()-2);

        g2.setClip(clipArea);

        switch (state) {
            case QUEUE:
                break;
            case EXTRACTING:
                g2.setColor(new Color(background.getRed() + 40 - 10*Integer.parseInt(progress),
                        background.getGreen() + 40 - 10*Integer.parseInt(progress),
                        background.getBlue() + 40 - 10*Integer.parseInt(progress)));
                g2.fillRect(xOff, 0, getHeight(), getPreferredSize().height);
                break;
            case RETRYING:
                g2.setColor(background);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                g2.setColor(green);
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
                g2.fillRect(xOff, getSize().height - (int) (getHeight()*(prog/track.ytDuration)), getHeight(), (int) (getHeight()*(prog/track.ytDuration)));
                break;
            case EXTRACTING_DONE:
            case DONE:
                g2.setColor(green);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                done = true;
                break;
            case ERROR:
                g2.setColor(Color.orange);
                g2.fillRect(xOff, 0, getHeight(), getHeight());
                break;
            case DOWNLOADING:
                g2.setColor(background);
                g2.fillRect(xOff, 0, (int) (getHeight() * (Double.parseDouble(progress.replaceAll("[^\\d.]", "")) / 100)), getHeight());
//                g2.setColor(Color.black);
//
//                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 8));
//                int y = jaudiotagger;
//                for (String line : speed.split("\n"))
//                    g2.drawString(line, jaudiotagger, y += g2.getFontMetrics().getHeight());
                break;
        }
        //g2.drawImage(mask, xOff, 0, null);
        g2.setClip(0,0,getWidth(),getHeight());
        clipArea = new Ellipse2D.Float(xOff+1,1,getHeight()-2,getHeight()-2);

        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.darkGray);
        g2.draw(clipArea);

        g2.dispose();

    }
}
