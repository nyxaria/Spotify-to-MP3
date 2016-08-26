package com.gedr;

import javax.swing.*;

public class Track {

    public String name, album;
    public String[] artists;
    public boolean explicit;
    public double popularity;
    public String target;
    public String videoTarget;
    public GraphicLabel gui;
    public int duration;
    public int ytDuration;

    public static int MAX_WIDTH;
    public String id = "";

    public Track(String name, String[] artists, String album, boolean explicit, int popularity, int dur) {
        this.name = name;
        this.artists = artists;
        this.album = album;
        this.explicit = explicit;
        this.popularity = popularity*.01;
        this.duration = dur;
    }
}
