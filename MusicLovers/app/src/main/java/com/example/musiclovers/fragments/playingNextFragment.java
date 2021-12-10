package com.example.musiclovers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.models.songItem;

import java.util.ArrayList;

/**
 *
 */
public class playingNextFragment extends Fragment implements MainActivity.UpdateFragmentPlayingNext {
    RecyclerView recyclerView;
    songsListAdapter adapter;
    ImageButton btnShuffle;
    ImageButton btnRepeat;
    TextView tvNoSongPlayingNext;
    ArrayList<songItem> nextSongs;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playing_next, container, false);
        recyclerView = v.findViewById(R.id.onPlayingRecyclerView);
        btnShuffle = v.findViewById(R.id.btnShuffle);
        btnRepeat = v.findViewById(R.id.btnRepeat);
        tvNoSongPlayingNext = v.findViewById(R.id.tvNoSongPlayingNext);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //BUTTON LISTENER
        btnRepeat.setOnClickListener(view1 -> {

        });

        btnShuffle.setOnClickListener(view12 -> {

        });
    }

    @Override
    public void initDisplayNextSongsList(ArrayList<songItem> songsList, int position) {
        nextSongs = new ArrayList<>();
        nextSongs.addAll( ((MainActivity) getContext()).songList);
        nextSongs.remove(0);

        if(nextSongs.size() == position){
            tvNoSongPlayingNext.setVisibility(View.VISIBLE);
        }else{
            adapter = new songsListAdapter(
                    R.layout.song_format,
                    R.id.song_format_SongName,
                    R.id.song_format_ArtistName,
                    R.id.song_format_SongImg,
                    nextSongs,
                    getContext()
            );
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void updateDisplayNextSongsList(Boolean isNext, Boolean isPrevious, songItem song) {
        if(isNext){
            nextSongs.remove(0);
        } else if(isPrevious){
            nextSongs.add(0, song);
        }
        adapter.notifyDataSetChanged();
    }
}
