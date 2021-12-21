package com.example.musiclovers.models;

import java.util.Date;

public class songItem {
    private String _id;
    private String songName;
    private String artistName;
    private String[] artistId;
    private String albumId;
    private String genreId;
    private int likeCount;
    private String songImg;
    private String songSrc;
    private Date create_at;

    public songItem(String _id,String songName, String artistName, String[] artistId, String albumId, String genreId, int likeCount, String songImg, String songSrc) {
        this._id = _id;
        this.songName = songName;
        this.artistName = artistName;
        this.artistId = artistId;
        this.albumId = albumId;
        this.genreId = genreId;
        this.likeCount = likeCount;
        this.songImg = songImg;
        this.songSrc = songSrc;
    }

    public String get_id() { return _id; }

    public String getSongName() {
        return songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public String[] getArtistId() {
        return artistId;
    }

    public String getAlbumId() {
        return albumId;
    }

    public String getGenreId() {
        return genreId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public String getSongImg() {
        return songImg;
    }

    public String getSongSrc() {
        return songSrc;
    }

}