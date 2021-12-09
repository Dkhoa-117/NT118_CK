package com.example.musiclovers.models;

public class albumItem {

    private String _id;
    private String albumName;
    private String artistName;
    private String artistId;
    private String albumImg;
    private String genreId;

    public albumItem(String _id, String albumName, String artistName, String artistId, String albumImg, String genreId) {
        this._id = _id;
        this.albumName = albumName;
        this.artistName = artistName;
        this.artistId = artistId;
        this.albumImg = albumImg;
        this.genreId = genreId;
    }

    public String get_id() {
        return _id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getAlbumImg() {
        return albumImg;
    }

    public String getGenreId() {
        return genreId;
    }
}
