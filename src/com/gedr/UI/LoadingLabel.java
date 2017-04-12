package com.gedr.UI;

import com.gedr.Main;
import com.gedr.Managers.Global;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;

public class LoadingLabel extends JLabel {
    int degree = 0;

    public LoadingLabel(int i) {
        new Thread(() -> {
            while(Main.settingUp) {
                long now = System.currentTimeMillis();
                while(System.currentTimeMillis() < now + 10) {
                }
                degree++;
                repaint();
                revalidate();
            }
        }).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        Global.prettify(g2d);

        Color green = new Color(9, 166, 73);

        g2d.setStroke(new BasicStroke(getHeight() / 12));

        g2d.setColor(green);
        int r = Math.min(getWidth(), getHeight()) - 10;
        Arc2D arc = new Arc2D.Double(5, 5, r, r, (degree % 360), 300, Arc2D.OPEN);
        g2d.draw(arc);
        g2d.dispose();
    }
}
