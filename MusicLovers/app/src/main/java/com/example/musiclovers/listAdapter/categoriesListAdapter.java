package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.fragments.artistDetailFragment;
import com.example.musiclovers.fragments.playlistDetailFragment;
import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;
import com.example.musiclovers.signIn_signUpActivity.SaveSharedPreference;

import java.time.Duration;
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
public class categoriesListAdapter extends RecyclerView.Adapter<categoriesListAdapter.ViewHolder> {
    ArrayList<String> categories;
    Context context;

    public categoriesListAdapter(ArrayList<String> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public categoriesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.albums_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull categoriesListAdapter.ViewHolder holder, int position) {
        String currentItem = categories.get(position);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.childRecyclerView.setLayoutManager(layoutManager);
        holder.childRecyclerView.setHasFixedSize(true);
        holder.category.setText(currentItem);
        String base_Url = "http://10.0.2.2:3000/";
        /* init Retrofit */
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);

        if(categories.get(position).equals("New Music")) {
            //Call<List<songItem>> call = placeHolder.getSongs();
            Call<List<songItem>> call = placeHolder.getSongsByCategory("new-music");
            call.enqueue(new Callback<List<songItem>>() {
                @Override
                public void onResponse(Call<List<songItem>> call, Response<List<songItem>> response) {
                    if(!response.isSuccessful()) {
                        Toast.makeText(context, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<songItem> songItems = (ArrayList<songItem>) response.body();
                    songsListAdapter mAdapter = new songsListAdapter(
                            R.layout.album_format,
                            R.id.album_format_album_name,
                            R.id.album_format_artist_name,
                            R.id.album_format_image,
                            songItems,
                            3, /* add song to playing next & playlist AVAILABLE */
                            context);
                    holder.childRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onFailure(Call<List<songItem>> call, Throwable t) {
                    Toast.makeText(context, "error", Toast.LENGTH_LONG);
                }
            });
        }
        else if(categories.get(position).equals("New Albums")) {
            Call<List<albumItem>> call = placeHolder.getAlbumsByCategory("new-albums");
            call.enqueue(new Callback<List<albumItem>>() {
                @Override
                public void onResponse(Call<List<albumItem>> call, Response<List<albumItem>> response) {
                    if(!response.isSuccessful()) {
                        Toast.makeText(context, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<albumItem> albumItems = (ArrayList<albumItem>) response.body();
                    albumsListAdapter mAdapter = new albumsListAdapter(
                            R.layout.album_format,
                            R.id.album_format_album_name,
                            R.id.album_format_artist_name,
                            R.id.album_format_image,
                            albumItems,
                            context);
                    holder.childRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onFailure(Call<List<albumItem>> call, Throwable t) {
                    Toast.makeText(context, "error", Toast.LENGTH_LONG);
                }
            });

        } //category: New Albums
        else if(categories.get(position).equals("Best New Songs")) {
            holder.childRecyclerView.setNestedScrollingEnabled(false);
            holder.childRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);

            Call<List<songItem>> call = placeHolder.getSongsByCategory("best-new-songs");
            call.enqueue(new Callback<List<songItem>>() {
                @Override
                public void onResponse(Call<List<songItem>> call, Response<List<songItem>> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(context, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<songItem> songs = (ArrayList<songItem>) response.body();
                    RecyclerView.LayoutManager layoutManagerSongs = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                    holder.childRecyclerView.setHasFixedSize(true);
                    ArrayList<String> column = new ArrayList<>();
                    column.add("column 1");
                    column.add("column 2");
                    artistDetailFragment.topSongsAdapter topSongsAdapter = new artistDetailFragment.topSongsAdapter(context, songs, column);
                    holder.childRecyclerView.setLayoutManager(layoutManagerSongs);
                    holder.childRecyclerView.setAdapter(topSongsAdapter);
                    topSongsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<List<songItem>> call, Throwable t) {
                    Toast.makeText(context, "error", Toast.LENGTH_LONG);
                }
            });

        } //category: Best New Songs
        else if(categories.get(position).equals("Recently Played")){
            Call<List<playlistItem>> call = placeHolder.getPlaylistByUser_PlaylistNum(SaveSharedPreference.getId(context), 1);
            call.enqueue(new Callback<List<playlistItem>>() {
                @Override
                public void onResponse(Call<List<playlistItem>> call, Response<List<playlistItem>> response) {
                    if(response.isSuccessful()){
                        ArrayList<playlistItem> playlists = (ArrayList<playlistItem>) response.body();
                        Call<List<songItem>> call1 = placeHolder.getSongsByPlaylist(playlists.get(0).get_id());
                        call1.enqueue(new Callback<List<songItem>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<songItem>> call, @NonNull Response<List<songItem>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(context, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<songItem> songItems = (ArrayList<songItem>) response.body();
                                songsListAdapter mAdapter = new songsListAdapter(
                                        R.layout.album_format,
                                        R.id.album_format_album_name,
                                        R.id.album_format_artist_name,
                                        R.id.album_format_image,
                                        songItems,
                                        3,
                                        context);
                                holder.childRecyclerView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<songItem>> call, Throwable t) {
                                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<playlistItem>> call, Throwable t) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } //category: Recently Played
        else if(categories.get(position).equals("Your Favorite")){
            Call<List<playlistItem>> call = placeHolder.getPlaylistByUser_PlaylistNum(SaveSharedPreference.getId(context), 0);
            call.enqueue(new Callback<List<playlistItem>>() {
                @Override
                public void onResponse(Call<List<playlistItem>> call, Response<List<playlistItem>> response) {
                    if(response.isSuccessful()){
                        ArrayList<playlistItem> playlists = (ArrayList<playlistItem>) response.body();
                        Call<List<songItem>> call1 = placeHolder.getSongsByPlaylist(playlists.get(0).get_id());
                        call1.enqueue(new Callback<List<songItem>>() {
                            @Override
                            public void onResponse(@NonNull Call<List<songItem>> call, @NonNull Response<List<songItem>> response) {
                                if (!response.isSuccessful()) {
                                    Toast.makeText(context, "code: " + response.code(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                ArrayList<songItem> songItems = (ArrayList<songItem>) response.body();
                                songsListAdapter mAdapter = new songsListAdapter(
                                        R.layout.album_format,
                                        R.id.album_format_album_name,
                                        R.id.album_format_artist_name,
                                        R.id.album_format_image,
                                        songItems,
                                        3,
                                        context);
                                holder.childRecyclerView.setAdapter(mAdapter);
                            }

                            @Override
                            public void onFailure(@NonNull Call<List<songItem>> call, Throwable t) {
                                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<playlistItem>> call, Throwable t) {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        } //category: Your Favorite
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView category;
        public RecyclerView childRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.title);
            childRecyclerView = itemView.findViewById(R.id.childRecycleView);
        }
    }
}