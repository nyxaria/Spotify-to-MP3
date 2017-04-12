package com.gedr.UI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UButton extends ULabel implements MouseListener {

    public UButton(String text, U.Size size, U.Shape shape, boolean hover, U.Style style) {
        super(text, size, shape, hover, style);
        addMouseListener(this);
    }

    public UButton(String text, U.Size size, U.Shape shape, boolean hover) {
        super(text, size, shape, hover);
        addMouseListener(this);
    }

    public UButton(String text, U.Size size, U.Shape shape) {
        super(text, size, shape);
        addMouseListener(this);
    }

    public UButton(String text, U.Size size) {
        super(text, size);
        addMouseListener(this);
    }

    @Override public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
            darken(true);
            clicked(true);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
            clicked(false);
            darken(false);

    }

    @Override
    public void mouseEntered(MouseEvent e) {
            entered(true);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        entered(false);
        clicked(false);
    }
}
