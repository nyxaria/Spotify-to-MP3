package com.gedr.UI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

public class SpotifyCheckBox extends JLabel implements MouseListener {
    Image tick, tickHover, plus, plusHover, crossHover;

    boolean hover = false;
    public boolean checked = false;
    public boolean empty;
    public boolean exited;

    @Override
    public int getHeight() {
        return super.getHeight() - super.getHeight() % 2;
    }

    @Override
    public int getWidth() {
        return super.getWidth() - super.getWidth() % 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        if(empty)
            return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setColor(new Color(48, 48, 48));
        g2.setStroke(new BasicStroke(1));
        if(checked) {
            if(hover)
                if(exited)
                    g2.drawImage(crossHover, getWidth() / 2 - crossHover.getWidth(null) / 2, getHeight() / 2 - crossHover.getHeight(null) / 2, null);
                else
                    g2.drawImage(tickHover, getWidth() / 2 - tickHover.getWidth(null) / 2, getHeight() / 2 - tickHover.getHeight(null) / 2, null);
            else
                g2.drawImage(tick, getWidth() / 2 - tick.getWidth(null) / 2, getHeight() / 2 - tick.getHeight(null) / 2, null);
        } else {
            if(hover)
                g2.drawImage(plusHover, getWidth() / 2 - plusHover.getWidth(null) / 2, getHeight() / 2 - plusHover.getHeight(null) / 2, null);
            else
                g2.drawImage(plus, getWidth() / 2 - plus.getWidth(null) / 2, getHeight() / 2 - plus.getHeight(null) / 2, null);
        }
    }

    public SpotifyCheckBox(String s) {
        super(s);
        this.addMouseListener(this);

        try {
            tick = ImageIO.read(getClass().getResourceAsStream("/check.png"));
            tickHover = ImageIO.read(getClass().getResourceAsStream("/check_hover.png"));
            plus = ImageIO.read(getClass().getResourceAsStream("/plus.png"));
            plusHover = ImageIO.read(getClass().getResourceAsStream("/plus_hover.png"));
            crossHover = ImageIO.read(getClass().getResourceAsStream("/cross.png"));

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        checked = !checked;
        exited = false;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();

    }

    @Override
    public void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
        exited = true;

    }
}