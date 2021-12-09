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

import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.songItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        if(categories.get(position).equals("New Musics")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
            Call<List<songItem>> call = placeHolder.getSongs();
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
                            context);
                    holder.childRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onFailure(Call<List<songItem>> call, Throwable t) {
                    Toast.makeText(context, "error", Toast.LENGTH_LONG);
                }
            });
        }
        if(categories.get(position).equals("New Albums")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
            Call<List<albumItem>> call = placeHolder.getAlbums();
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

        }
        if(categories.get(position).equals("Today Top Hit")) {

        }
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