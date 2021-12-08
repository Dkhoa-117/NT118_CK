package com.example.musiclovers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.playlistItem;

public class ViewModel extends androidx.lifecycle.ViewModel {
    // TODO: Implement the ViewModel -> communicate between fragments
    //ALBUM______________
    private final MutableLiveData<albumItem> selectedAlbum = new MutableLiveData<albumItem>();
    public void select(albumItem album) {
        selectedAlbum.setValue(album);
    }
    public LiveData<albumItem> getSelectedAlbum() {
        return selectedAlbum;
    }

    //PLAYLIST______________
    private final MutableLiveData<playlistItem> selectedPlaylist = new MutableLiveData<playlistItem>();
    public void select(playlistItem playlistItem) {
        selectedPlaylist.setValue(playlistItem);
    }
    public LiveData<playlistItem> getSelectedPlaylist() {
        return selectedPlaylist;
    }

    //ARTIST______________
    private final MutableLiveData<artistItem> selectedArtist = new MutableLiveData<artistItem>();
    public void select(artistItem artistItem) {
        selectedArtist.setValue(artistItem);
    }
    public LiveData<artistItem> getSelectedArtist() {
        return selectedArtist;
    }
}