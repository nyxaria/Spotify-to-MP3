package com.gedr;

import javax.swing.*;
import java.awt.*;

class Separator extends JComponent {
    public static final Color seperatorColor = new Color(48,48,48);
    private boolean thick;

    public Separator(boolean thick) {
        setOpaque(true);
        this.thick = thick;
        if(thick) {
            setPreferredSize(new Dimension(getPreferredSize().width, 8));
        }
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), 2);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(seperatorColor);
        if(!thick) {
            g.fillRect(0, 0, getWidth() - 5, 2);
        } else {
            g.fillRect(10, getHeight()/2-1, getWidth() - 10, 2);
        }
    }
}