package com.gedr.UI;

import com.gedr.Managers.Global;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class SpotifyScroll extends BasicScrollBarUI {

    Color background = Global.darkerGray;

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(background);
        g.fillRect(0, 0, (int) trackBounds.getWidth(), (int) trackBounds.getHeight());
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = new JButton("zero button");
        Dimension zeroDim = new Dimension(0, 0);
        button.setPreferredSize(zeroDim);
        button.setMinimumSize(zeroDim);
        button.setMaximumSize(zeroDim);
        return button;
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2d = (Graphics2D) g;
        Global.prettify(g2d);

        g2d.setColor(new Color(52, 52, 52));
        g2d.translate(thumbBounds.x, thumbBounds.y);
        g2d.fillRoundRect(4, 3, (int) thumbBounds.getWidth() - 7, (int) thumbBounds.getHeight() - 6, 10, 8);
        g2d.translate(-thumbBounds.x, -thumbBounds.y);
        g2d.dispose();

    }
}
