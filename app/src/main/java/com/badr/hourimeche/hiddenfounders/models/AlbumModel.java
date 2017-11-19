package com.badr.hourimeche.hiddenfounders.models;

public class AlbumModel {

    private String idAlbum;
    private String nameAlbum;

    public AlbumModel(String idAlbum, String nameAlbum) {
        this.idAlbum = idAlbum;
        this.nameAlbum = nameAlbum;
    }

    public String getIdAlbum() {
        return idAlbum;
    }

    public String getNameAlbum() {
        return nameAlbum;
    }

}
