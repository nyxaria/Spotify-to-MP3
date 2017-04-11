package com.gedr.Modules;


import java.awt.*;

/**
 * Created by gedr on 29/07/2016.
 */
public class Playlist {
    public String name, id, owner;
    public Image[] images;
    public Track[] tracks;
    public int total;

    public Playlist(String name, String id, String owner, int total, Image[] images) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.images = images;
        this.total = total;
    }
}
