package com.example.musiclovers.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.ViewModel;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class playlistDetailFragment extends Fragment {
    RecyclerView mRecyclerView;
    songsListAdapter mAdapter;
    public playlistItem playlist;
    PlaceHolder placeHolder;
    ViewModel viewModel;
    ExtendedFloatingActionButton btnPlayAll;
    FloatingActionButton addSong;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist_detail, container, false);
        btnPlayAll = v.findViewById(R.id.fragment_playlist_detail_PLayBtn);
        addSong = v.findViewById(R.id.fragment_playlist_detail_AddSong);
        collapsingToolbarLayout = v.findViewById(R.id.fragment_playlist_detail_CollapsingToolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.rgb(138, 48, 57));
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Passing data from Playlist fragment
        viewModel.getSelectedPlaylist().observe(getViewLifecycleOwner(), playlistItem -> {
            playlist = playlistItem;
            CharSequence sequence = (playlistItem.getPlaylistName());
            collapsingToolbarLayout.setTitle(sequence);
            ImageView playlistImg = view.findViewById(R.id.fragment_playlist_detail_PlaylistImg);
            mRecyclerView = view.findViewById(R.id.fragment_playlist_detail_PlaylistRecycleView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(playlistDetailFragment.this.getContext()));
            String base_Url = "http://10.0.2.2:3000/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            placeHolder = retrofit.create(PlaceHolder.class);
            new DownloadImageTask(playlistImg).execute(base_Url + playlistItem.getPlaylistImg());
            //Loading playlist Songs
            Call<List<songItem>> call = placeHolder.getSongsByPlaylist(playlistItem.get_id());
            call.enqueue(new Callback<List<songItem>>() {
                @Override
                public void onResponse(@NonNull Call<List<songItem>> call, @NonNull Response<List<songItem>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<songItem> songItems = (ArrayList<songItem>) response.body();

                    mAdapter = new songsListAdapter(
                            R.layout.song_format,
                            R.id.song_format_SongName,
                            R.id.song_format_ArtistName,
                            R.id.song_format_SongImg,
                            songItems,
                            1, /* remove from playlist */
                            getContext());
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.base = playlistDetailFragment.this;
                    btnPlayAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view1) {
                            ((MainActivity) getActivity()).getSongs(songItems, 0);
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<List<songItem>> call, Throwable t) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            });

            //Adding Song
            addSong.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view1) {

                }
            });
        });
    }
}
