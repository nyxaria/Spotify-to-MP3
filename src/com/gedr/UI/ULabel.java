package com.gedr.UI;

import com.gedr.Main;
import com.gedr.Managers.Global;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ULabel extends JLabel implements MouseListener {

    private String text;
    private int darken;
    private int padding = 2;
    private int squareX;
    private boolean mouseOver;
    private boolean mouseDown;
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(hover) {
            setForeground(new Color(Math.abs(inverted - 65 - darken), Math.abs(inverted - 65 - darken), Math.abs(inverted - 65 - darken)));
        }
        //Main.leftScrollPane.getVerticalScrollBar().repaint();

    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseOver = false;
        if(hover) {
            setForeground(new Color(Math.abs(inverted - 20), Math.abs(inverted - 20), Math.abs(inverted - 20)));
        }

    }

    public void entered(boolean b) {
        mouseOver = b;
        repaint();
        revalidate();

    }

    public void clicked(boolean b) {
        mouseDown = b;
        repaint();
        revalidate();

    }

    public void darken(boolean b) {
        darken = b ? 30 : 0;
        setForeground(new Color(Math.abs(inverted - 65 - darken), Math.abs(inverted - 65 - darken), Math.abs(inverted - 65 - darken)));
    }

    private int sizeInt;
    private int inverted;
    private Font font;
    private boolean hover;

    private Dimension baselinePixelDims;

    public ULabel(String text, U.Size size, U.Shape shape, boolean hover, U.Style style) {
        super(text);
        init(text, size, shape, hover, style);
    }

    public ULabel(String text, U.Size size, U.Shape shape, boolean hover) {
        super(text);
        init(text, size, shape, hover, U.Style.Plain);
    }

    public ULabel(String text, U.Size size, U.Shape shape) {
        super(text);
        init(text, size, shape, true, U.Style.Plain);
    }

    public ULabel(String text, U.Size size) {
        super(text);
        init(text, size, U.Shape.Normal, true, U.Style.Plain);
    }

    public void inverted(boolean b) {
        inverted = b ? 235 : 0;
        setForeground(new Color(Math.abs(inverted - 20 - darken), Math.abs(inverted - 20 - darken), Math.abs(inverted - 20 - darken)));
    }

    public void init(String text, U.Size size, U.Shape shape, boolean hover, U.Style style) {
        this.text = text;

        if(text.startsWith("icon:")) setText("");
        this.hover = hover;
        switch(size) {
            case Tiny:
                sizeInt = 10;
                squareX = 10;
                break;
            case Small:
                sizeInt = 13;
                squareX = 22;
                break;
            case Smaller:
                sizeInt = 13;
                squareX = 20;
                break;
            case Big:
                sizeInt = 30;
                squareX = 40;
                break;
            case Bigger:
                sizeInt = 40;
                squareX = 50;
                break;
            case Normal:
            default:
                squareX = 20;
                sizeInt = 19;
        }

        switch(style) {
            case Bold:
                font = Global.fontBold.deriveFont((float) sizeInt);
                break;
            case Itallic:
                font = Global.fontNormal.deriveFont((float) sizeInt);
                break;
            case Plain:
            default:
                font = Global.fontNormal.deriveFont((float) sizeInt);
        }

        //baselinePixelDims = font.getStringBounds(text, ((Graphics2D) new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics()).getFontRenderContext());

        switch(shape) {
            case Square:
                if(text.startsWith("icon:")) {
                    setPreferredSize(new Dimension(squareX, squareX));
                    repaint();
                    revalidate();
                } else {
                    squareX = (int) Math.max(getPreferredSize().getWidth(), getPreferredSize().getHeight());
                    setPreferredSize(new Dimension(squareX + padding, squareX + padding));
                    setHorizontalAlignment(SwingConstants.CENTER);
                }
        }

        setFont(font.deriveFont((float) sizeInt));
        setBorder(new CompoundBorder(getBorder(), new EmptyBorder(2, 4, 4, 4)));
        addMouseListener(this);

    }

    public Dimension getTextDims() {
        return baselinePixelDims;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(!text.startsWith("icon:")) return;
        Graphics2D g2 = (Graphics2D) g;
        Global.prettify(g2);

        switch(text.substring(text.indexOf(":") + 1, text.length())) {
            case "x":

                g2.setColor(Color.gray);
                g2.drawOval(5, 5, squareX - 10, squareX - 10);
                g2.setColor(Color.lightGray);

                if(mouseDown)
                    g2.setColor(Color.lightGray.darker());
                g2.fillOval(5,5, squareX - 10, squareX - 10);
                g2.setColor(Color.darkGray);
                if(mouseOver) {
                    g2.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(8, 8, squareX - 8, squareX - 8);
                    g2.drawLine(8, squareX - 8, squareX - 8, 8);
                }

                break;
            case "image_picture":

                g2.setColor(Color.black);
                g2.drawRect(squareX/6, squareX*2/10, squareX*4/6, squareX*6/9);
                g2.setColor(Color.white);
                g2.fillRect(squareX/6, squareX*2/10, squareX*4/6, squareX*6/9);

                if(mouseDown)
                    g2.setPaint(new GradientPaint(0,0,Color.YELLOW,0, 20,Color.RED));
                else
                    g2.setColor(new Color(135,206,250));
                g2.fillRect(squareX/6 + 1, squareX*2/10 + 1, squareX*4/6 - 2, squareX*2*6/9/3);
                if(mouseDown)
                    g2.setColor(Color.orange);
                else
                    g2.setColor(Color.yellow);
                g2.fillOval(squareX/6 + squareX*80/500, squareX*53/200 + squareX/8, 3, 3);
                //g2.drawRect(0,0,squareX,squareX);
                g2.setColor(Color.lightGray.darker());
                g2.fillPolygon(new int[]{squareX/6 + squareX*73/50/3, squareX/6 + squareX*4/6 - 1, squareX/6 + squareX*4/6 - 1, squareX/6 + squareX*4/6/5, squareX/6 + squareX*73/50/3},
                        new int[]{squareX*2/10 + squareX*4*6/10/3/4, squareX*2/10 + squareX*2*6/12/3, squareX*2/10 + squareX*2*6/9/3 + 1, squareX*2/10 + squareX*2*6/9/3 + 1, squareX*2/10 + squareX*4*6/10/3/4}, 5);
                g2.setColor(Color.white);
//                g2.fillPolygon(new int[]{squareX/6 + squareX*73/50/3, squareX/6 + squareX*4/6 - 1, squareX/6 + squareX*4/6 - 1, squareX/6 + squareX*4/6/5, squareX/6 + squareX*73/50/3},
                    //new int[]{squareX*2/10 + squareX*4*6/10/3/4, squareX*2/10 + squareX*2*6/12/3, squareX*2/10 + squareX*2*6/9/3 + 1, squareX*2/10 + squareX*2*6/9/3 + 1, squareX*2/10 + squareX*4*6/10/3/4}, 5);

        }
        g.dispose();

    }


}
