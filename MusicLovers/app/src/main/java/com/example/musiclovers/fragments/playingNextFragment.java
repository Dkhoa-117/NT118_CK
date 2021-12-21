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
import java.util.Collection;
import java.util.Collections;

/**
 * DONE
 */
public class playingNextFragment extends Fragment implements MainActivity.UpdateFragmentPlayingNext {
    RecyclerView recyclerView;
    ImageButton btnShuffle;
    ImageButton btnRepeat;
    TextView tvNoSongPlayingNext;
    public ArrayList<songItem> nextSongs = new ArrayList<>();
    boolean isShuffle;
    ArrayList<songItem> tempSongArray = new ArrayList<>();
    public songsListAdapter adapter;

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

        init();
        //BUTTON LISTENER
        btnRepeat.setOnClickListener(view1 -> {
            if(((MainActivity) getContext()).isRepeat){
                ((MainActivity) getContext()).isRepeat = false;
                btnRepeat.setImageResource(R.drawable.ic_repeat_off);
            }else{
                ((MainActivity) getContext()).isRepeat = true;
                btnRepeat.setImageResource(R.drawable.ic_repeat);
            }
        });

        btnShuffle.setOnClickListener(view2 -> {
            if(!nextSongs.isEmpty()){
                if(isShuffle) {
                    shuffleTurnOff();
                }else{
                    shuffleTurnOn();
                }
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void shuffleTurnOn() {
        isShuffle = true;
        btnShuffle.setImageResource(R.drawable.ic_shuffle);
        //update position
        ((MainActivity) getContext()).position = 0;
        //update tempArray - fragment
        tempSongArray.clear();
        tempSongArray.addAll(((MainActivity) getContext()).songList);
        //update nextSongs
        Collections.shuffle(nextSongs);
        //update songList - MainActivity
        ((MainActivity) getContext()).songList.clear();
        ((MainActivity) getContext()).songList.add(tempSongArray.get(((MainActivity) getContext()).position));
        ((MainActivity) getContext()).songList.addAll(nextSongs);
    }

    private void shuffleTurnOff() {
        isShuffle = false;
        btnShuffle.setImageResource(R.drawable.ic_shuffle_off);
        //update position
        int temp = tempSongArray.indexOf(((MainActivity) getContext()).songList.get(((MainActivity) getContext()).position));
        ((MainActivity) getContext()).position = temp;
        //update songList - MainActivity
        ((MainActivity) getContext()).songList.clear();
        ((MainActivity) getContext()).songList.addAll(tempSongArray);
        //update nextSongs
        nextSongs.clear();
        nextSongs.addAll( ((MainActivity) getContext()).songList);
        if (temp >= 0) {
            nextSongs.subList(0, temp + 1).clear();
        }
    }

    @Override
    public void initDisplayNextSongsList(ArrayList<songItem> songsList, int position) {
        if(isShuffle){
            tempSongArray.clear();
            tempSongArray.addAll(((MainActivity) getContext()).songList);
            Collections.shuffle(((MainActivity) getContext()).songList);
        }
        nextSongs.clear();
        nextSongs.addAll( ((MainActivity) getContext()).songList);
        if(nextSongs.size() != 0){
            nextSongs.remove(0);

            if(nextSongs.size() == position){
                tvNoSongPlayingNext.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else{
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
            }
        }
    }

    @Override
    public void updateDisplayNextSongsList(Boolean isNext, Boolean isPrevious, songItem song) {
        if(!nextSongs.isEmpty()){
            if(isNext){
                nextSongs.remove(0);
            } else if(isPrevious){
                nextSongs.add(0, song);
            }
            adapter.notifyDataSetChanged();
        }
    }

    void init(){
        adapter  = new songsListAdapter(
                R.layout.song_format,
                R.id.song_format_SongName,
                R.id.song_format_ArtistName,
                R.id.song_format_SongImg,
                nextSongs,
                2, /* remove from playing next */
                getContext()
        );
    }
}
