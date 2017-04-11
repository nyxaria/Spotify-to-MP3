package com.gedr.Managers;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Global {

    public static final Color spotifyGreen = new Color(9,166,73);
    public static final Color darkGray = new Color(38,38,38);
    public static final Color darkerGray = new Color(28,28,28);
    public static final Color offWhite = new Color(240,240,240);

    public static String youtube_dl;
    public static String ffmpeg;

    public static Font fontThin;
    public static Font fontNormal;
    public static Font fontBold;


    protected final static String clientId = "d81b37798a5b435bb1fb6ea1cc9369d6";
    protected final static String secret = "72c92e7ffab04d16992a949efeeaca9f";

    public static void prettify(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    public static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }
}
