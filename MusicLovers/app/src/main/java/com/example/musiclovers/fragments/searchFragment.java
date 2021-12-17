package com.example.musiclovers.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.albumsListAdapter;
import com.example.musiclovers.listAdapter.artistsAdapter;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.songItem;
import com.google.android.material.tabs.TabLayout;

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
public class searchFragment extends Fragment {
    SearchView searchView;
    RecyclerView recyclerView;
    ArrayList<songItem> songItems = new ArrayList<>();
    ArrayList<albumItem> albumItems = new ArrayList<>();
    ArrayList<artistItem> artistItems = new ArrayList<>();
    songsListAdapter songsAdapter;
    albumsListAdapter albumsAdapter;
    artistsAdapter artistsAdapter;
    TextView textViewThumbnail, textViewSearchNotFound;
    TabLayout tabBar;
    Retrofit retrofit;
    PlaceHolder placeHolder;
    String searchString = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_search, container, false);
        searchView = v.findViewById(R.id.fragment_search_SearchView);
        recyclerView = v.findViewById(R.id.fragment_search_SearchRecycleView);
        textViewThumbnail = v.findViewById(R.id.search_thumbnail);
        textViewSearchNotFound = v.findViewById(R.id.tv_search_not_found);
        tabBar = v.findViewById(R.id.tabLayout);
        songItems = new ArrayList<>();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout.Tab tabSongs = tabBar.newTab().setText("Songs");
        tabBar.addTab(tabSongs);
        TabLayout.Tab tabArtists = tabBar.newTab().setText("Artists");
        tabBar.addTab(tabArtists);
        TabLayout.Tab tabAlbums = tabBar.newTab().setText("Albums");
        tabBar.addTab(tabAlbums);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchString = query;
                if(tabSongs.isSelected())
                    searchSongs(query);
                else if(tabAlbums.isSelected())
                    searchAlbums(query);
                else if(tabArtists.isSelected())
                    searchArtists(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!songItems.isEmpty()){
                    songItems.clear();
                    textViewThumbnail.setVisibility(View.INVISIBLE);
                    songsAdapter.notifyDataSetChanged();
                }else {
                    textViewSearchNotFound.setVisibility(View.GONE);
                }
                return false;
            }
        });

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab == tabSongs){
                    if(!songItems.isEmpty()){
                        recyclerView.setAdapter(songsAdapter);
                    }else if (!searchString.isEmpty()){
                        searchSongs(searchString);
                    }
                }else if(tab == tabAlbums){
                    if(!albumItems.isEmpty()){
                        recyclerView.setAdapter(albumsAdapter);
                    }else if(!searchString.isEmpty()){
                        searchAlbums(searchString);
                    }
                }else if(tab == tabArtists){
                    if(!artistItems.isEmpty()){
                        recyclerView.setAdapter(artistsAdapter);
                    }else if(!searchString.isEmpty()){
                        searchArtists(searchString);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    void searchSongs(String query){
        Call<List<songItem>> call = placeHolder.searchSongs(query);
        call.enqueue(new Callback<List<songItem>>() {
            @Override
            public void onResponse(Call<List<songItem>> call, Response<List<songItem>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                songItems = (ArrayList<songItem>) response.body();
                if(songItems.isEmpty()) {
                    textViewSearchNotFound.setVisibility(View.VISIBLE);
                }
                songsAdapter = new songsListAdapter(
                        R.layout.song_format,
                        R.id.song_format_SongName,
                        R.id.song_format_ArtistName,
                        R.id.song_format_SongImg,
                        songItems,
                        3, /* add song to playing next & playlist AVAILABLE */
                        getContext());
                recyclerView.setAdapter(songsAdapter);
                textViewThumbnail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<songItem>> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
    }
    void searchArtists(String query){
        Call<List<artistItem>> call = placeHolder.searchArtists(query);
        call.enqueue(new Callback<List<artistItem>>() {
            @Override
            public void onResponse(Call<List<artistItem>> call, Response<List<artistItem>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                artistItems = (ArrayList<artistItem>) response.body();
                if(artistItems.isEmpty()) {
                    textViewSearchNotFound.setVisibility(View.VISIBLE);
                }
                artistsAdapter = new artistsAdapter(
                        R.layout.artist_format,
                        R.id.artist_format_ArtistName,
                        R.id.artist_format_ArtistImg,
                        artistItems,
                        getContext());
                recyclerView.setAdapter(artistsAdapter);
                textViewThumbnail.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(Call<List<artistItem>> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
    }
    void searchAlbums(String query){
        Call<List<albumItem>> call = placeHolder.searchAlbums(query);
        call.enqueue(new Callback<List<albumItem>>() {
            @Override
            public void onResponse(Call<List<albumItem>> call, Response<List<albumItem>> response) {
                if(!response.isSuccessful()) {
                    Toast.makeText(getContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                albumItems = (ArrayList<albumItem>) response.body();
                if(albumItems.isEmpty()) {
                    textViewSearchNotFound.setVisibility(View.VISIBLE);
                }
                albumsAdapter = new albumsListAdapter(
                        R.layout.song_format,
                        R.id.song_format_SongName,
                        R.id.song_format_ArtistName,
                        R.id.song_format_SongImg,
                        albumItems,
                        getContext());
                recyclerView.setAdapter(albumsAdapter);
                textViewThumbnail.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<albumItem>> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
    }
}

