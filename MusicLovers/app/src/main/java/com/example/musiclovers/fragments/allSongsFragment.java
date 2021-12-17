package com.example.musiclovers.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.models.songItem;

import java.util.ArrayList;

public class allSongsFragment extends Fragment {
    ArrayList<songItem> songItems = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        songItems = (ArrayList<songItem>) getArguments().getSerializable("songsList");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView songsRecyclerView = view.findViewById(R.id.fragment_all_songs_RecyclerView);
        songsRecyclerView.setHasFixedSize(true);
        songsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        songsListAdapter songsListAdapter = new songsListAdapter(
                R.layout.song_format,
                R.id.song_format_SongName,
                R.id.song_format_ArtistName,
                R.id.song_format_SongImg,
                songItems,
                3, /* add song to playing next & playlist AVAILABLE */
                getContext()
        );
        songsRecyclerView.setAdapter(songsListAdapter);
    }
}
