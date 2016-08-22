package com.gedr;


import java.awt.*;
import java.util.List;

/**
 * Created by gedr on 29/07/2016.
 */
public class Playlist {
    public String name, id, owner;
    public Image[] images;
    public Track[] tracks;

    public Playlist(String name, String id, String owner, Image[] images) {
        this.name = name;
        this.id = id;
        this.owner = owner;
        this.images = images;
    }
}
