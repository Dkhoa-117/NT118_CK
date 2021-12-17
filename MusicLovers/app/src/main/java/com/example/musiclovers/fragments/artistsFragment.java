package com.example.musiclovers.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.artistsAdapter;
import com.example.musiclovers.listAdapter.playlistAdapter;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.playlistItem;

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
public class artistsFragment extends Fragment {

    ArrayList<artistItem> artistItems = new ArrayList<>();
    PlaceHolder placeHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String base_Url = "http://10.0.2.2:3000/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
        Call<List<artistItem>> call = placeHolder.getArtists();
        call.enqueue(new Callback<List<artistItem>>() {
            @Override
            public void onResponse(Call<List<artistItem>> call, Response<List<artistItem>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "code: "+ response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                artistItems = (ArrayList<artistItem>) response.body();
                RecyclerView playlistRecyclerView = view.findViewById(R.id.fragment_artists_RecyclerView);
                playlistRecyclerView.setHasFixedSize(true);
                playlistRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                artistsAdapter artistAdapter = new artistsAdapter(
                        R.layout.artist_format,
                        R.id.artist_format_ArtistName,
                        R.id.artist_format_ArtistImg,
                        artistItems,
                        getContext()
                );
                playlistRecyclerView.setAdapter(artistAdapter);
            }

            @Override
            public void onFailure(Call<List<artistItem>> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG);
            }
        });
    }
}
