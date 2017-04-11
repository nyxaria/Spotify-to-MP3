package com.gedr.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class LoadingLabel extends JLabel {
    int degree = 0;

    public LoadingLabel(int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    long now = System.currentTimeMillis();
                    while(System.currentTimeMillis() < now + 10) {
                    }
                    degree++;
                    repaint();
                    revalidate();
                }
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color green = new Color(9, 166, 73);

        g2d.setStroke(new BasicStroke(getHeight() / 12));

        g2d.setColor(green);
        int r = Math.min(getWidth(), getHeight()) - 10;
        Arc2D arc = new Arc2D.Double(5, 5, r, r, (degree % 360), 300, Arc2D.OPEN);
        g2d.draw(arc);
        g2d.dispose();
    }
}
