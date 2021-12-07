package com.example.musiclovers.models;

public class playlistItem {
    private String _id;
    private String playlistName;
    private String userId;
    private int playlist_number;
    private String playlistImg;
    private String[] songId;
    private int numSongs;

    public String get_id() {
        return _id;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public String getUserId() {
        return userId;
    }

    public String getPlaylistImg() {
        return playlistImg;
    }

    public String[] getSongId() {
        return songId;
    }

    public int getNumSongs() {
        return numSongs;
    }

    public playlistItem(String _id, String playlistName, String userId, String playlistImg, String[] songId, int numSongs) {
        this._id = _id;
        this.playlistName = playlistName;
        this.userId = userId;
        this.playlistImg = playlistImg;
        this.songId = songId;
        this.numSongs = numSongs;
    }
}
