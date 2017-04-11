package com.wrapper.spotify.models;

public enum AlbumType {

  ALBUM("album"),
  NULL("NULL"),
  SINGLE("single"),
  COMPILATION("compilation");

  public final String type;

  AlbumType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

}
