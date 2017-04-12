package com.gedr.UI;

import com.gedr.Managers.Global;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class HintTextField extends JTextField implements FocusListener {

    private final String hint;
    private boolean showingHint;
    public Thread thread;
    public boolean running;
    private double degree;

    private Shape shape;

    private int round = 13;
    private int shadeWidth = 1;
    private int textSpacing = 3;
    public boolean loading;
    Color green = new Color(9, 166, 73);
    public Color red = new Color(255, 0, 0, 0);


    public HintTextField(final String hint) {
        super(hint);
        setFont(Global.fontNormal.deriveFont(13.2f));
        this.hint = hint;
        this.showingHint = true;
        setForeground(Color.gray);
        setOpaque(false);
        int insets = shadeWidth + 1 + textSpacing;
        setBorder(BorderFactory.createEmptyBorder(insets, insets + 6, insets, insets + 6));

        DefaultCaret dc = new DefaultCaret() {
            @Override
            public void paint(Graphics g) {
                if(isVisible()) {

                    JTextComponent comp = getComponent();
                    if(comp == null) {
                        return;
                    }

                    Rectangle r = null;
                    try {
                        r = comp.modelToView(getDot());
                        if(r == null) {
                            return;
                        }
                    } catch(BadLocationException e) {
                        return;
                    }
                    if(isVisible()) {
                        g.setColor(new Color(100,100,100, 255));
                        g.fillRect(r.x, r.y , 1, r.height);
                    }
                }
            }
        };

        dc.setBlinkRate(600);
        setCaret(dc);

        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText("");
            setForeground(Color.black);
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText(hint);
            setForeground(Color.GRAY);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }


    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape border = getBorderShape();

        Stroke os = g2d.getStroke();
        g2d.setStroke(new BasicStroke(shadeWidth * 3));
        g2d.setPaint(Color.BLACK);

        g2d.draw(border);
        g2d.setStroke(os);

        g2d.setPaint(Color.WHITE);
        g2d.fill(border);
        g2d.setPaint(Color.WHITE);

        if(loading) {
            if(!running) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(loading) {
                            long now = System.currentTimeMillis();
                            while(System.currentTimeMillis() < now + 2) {
                            }
                            degree += .2;
                            repaint();
                            revalidate();
                        }
                    }
                });
                thread.start();
                running = true;
            }
            g2d.setStroke(new BasicStroke(2));

            g2d.setColor(green);
            int r = Math.min(getWidth(), getHeight()) - 10;
            Arc2D arc = new Arc2D.Double(getWidth() - r - 6, 5, r, r, (degree % 360), 300, Arc2D.OPEN);
            g2d.draw(arc);
        }

        if(red.getAlpha() > 0) {
            g2d.setColor(red);
            g2d.setFont(Global.fontBold.deriveFont(16f));
            g2d.drawString("!", getWidth() - 17, 20);
        }

        super.paintComponent(g);
    }

    private Shape getBorderShape() {
        JTextComponent component = this;
        if(round > 0) {
            return new RoundRectangle2D.Double(shadeWidth, shadeWidth, component.getWidth() - shadeWidth * 2 - 1, component.getHeight() - shadeWidth * 2 - 1, round * 2, round * 2);
        } else {
            return new Rectangle2D.Double(shadeWidth, shadeWidth, component.getWidth() - shadeWidth * 2 - 1, component.getHeight() - shadeWidth * 2 - 1);
        }
    }

    public boolean contains(int x, int y) {
        if(shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        return shape.contains(x, y);
    }

    public void startAnimation() {
        new Thread(() -> {
            while(red.getAlpha() > 5) {
                long now = System.currentTimeMillis();
                while(System.currentTimeMillis() < now + 18) {
                }
                red = new Color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), red.getAlpha() - 5);
                repaint();
                revalidate();
            }

        }).start();
    }
}
