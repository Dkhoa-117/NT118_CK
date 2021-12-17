package com.example.musiclovers.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.ViewModel;
import com.example.musiclovers.listAdapter.albumsListAdapter;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.songItem;
import com.google.android.material.appbar.CollapsingToolbarLayout;

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
public class artistDetailFragment extends Fragment {
    private TextView seeMoreSongs;
    private RecyclerView albumsRecyclerView;
    private RecyclerView recyclerView;
    private ImageView artistImg;
    private PlaceHolder placeHolder;
    private ViewModel viewModel;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private String base_Url = "http://10.0.2.2:3000/";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_artist_detail, container, false);

        //find View by ID
        seeMoreSongs = v.findViewById(R.id.fragment_artist_detail_see_more_songs);
        albumsRecyclerView = v.findViewById(R.id.fragment_artist_detail_ArtistAlbumsRecycleView);
        recyclerView = v.findViewById(R.id.fragment_artist_detail_ArtistTopSongsRecycleView);
        artistImg = v.findViewById(R.id.fragment_artist_detail_PlaylistImg);
        collapsingToolbarLayout = v.findViewById(R.id.fragment_artist_detail_CollapsingToolbar);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placeHolderSetup();
        /* get value from Parent fragment */
        viewModel.getSelectedArtist().observe(getViewLifecycleOwner(), artistItem -> {
            collapsingToolbarLayout.setTitle(artistItem.getArtistName());
            new DownloadImageTask(artistImg).execute(base_Url + artistItem.getArtistImg());

            Call<List<albumItem>> callAlbums = placeHolder.getAlbumsByArtist(artistItem.get_id());
            callAlbums.enqueue(new Callback<List<albumItem>>() {
                @Override
                public void onResponse(Call<List<albumItem>> call, Response<List<albumItem>> response) {
                    if(response.isSuccessful()){
                        ArrayList<albumItem> albumItems;
                        albumItems = (ArrayList<albumItem>) response.body();
                        /* albums */
                        albumsListAdapter albumsAdapter = new albumsListAdapter(
                                R.layout.album_format,
                                R.id.album_format_album_name,
                                R.id.album_format_artist_name,
                                R.id.album_format_image,
                                albumItems,
                                getContext());
                        RecyclerView.LayoutManager layoutManagerAlbums = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
                        recyclerView.setHasFixedSize(true);
                        albumsRecyclerView.setLayoutManager(layoutManagerAlbums);
                        albumsRecyclerView.setAdapter(albumsAdapter);
                    }
                }

                @Override
                public void onFailure(Call<List<albumItem>> call, Throwable t) {
                    //Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            });

            Call<List<songItem>> callSongs = placeHolder.getSongsByArtist(artistItem.get_id());
            callSongs.enqueue(new Callback<List<songItem>>() {
                @Override
                public void onResponse(Call<List<songItem>> call, Response<List<songItem>> response) {
                    if(response.isSuccessful()){
                        ArrayList<songItem> songItems;
                        songItems = (ArrayList<songItem>) response.body();
                        handlerSeeMore(songItems);

                        /* top songs */
                        RecyclerView.LayoutManager layoutManagerSongs = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
                        recyclerView.setHasFixedSize(true);
                        ArrayList<String> categories = new ArrayList<>();
                        categories.add("column 1");
                        categories.add("column 2");
                        topSongsAdapter topSongsAdapter = new topSongsAdapter(getContext(), songItems, categories);
                        recyclerView.setLayoutManager(layoutManagerSongs);
                        recyclerView.setAdapter(topSongsAdapter);
                        topSongsAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<List<songItem>> call, Throwable t) {
                    Toast.makeText(getContext(), "error at songs", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void handlerSeeMore(ArrayList<songItem> songs){
        /* see more songs */
        seeMoreSongs.setOnClickListener(view -> {
            allSongsFragment fragment = new allSongsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("songsList", songs);
            fragment.setArguments(bundle);
            ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();
        });
    }

    private void placeHolderSetup(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
    }

    public static class topSongsAdapter extends RecyclerView.Adapter<topSongsAdapter.ViewHolder> {
        ArrayList<String> categories;
        Context context;
        ArrayList<songItem> songItems;
        public topSongsAdapter(Context context, ArrayList<songItem> songItems, ArrayList<String> categories) {
            this.context = context;
            this.songItems = songItems;
            this.categories = categories;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ArrayList<songItem> songItems1 = new ArrayList<>();
            ArrayList<songItem> songItems2 = new ArrayList<>();
            for(int i = 0; i < songItems.size(); i++){
                if(i < 4){
                    songItems1.add(songItems.get(i));
                }else if(i < 8){
                    songItems2.add(songItems.get(i));
                }
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.childRecyclerView.setLayoutManager(layoutManager);
            holder.childRecyclerView.setHasFixedSize(true);

            if(categories.get(position).equals("column 1")) {
                songsListAdapter mAdapter = new songsListAdapter(
                        R.layout.song_format,
                        R.id.song_format_SongName,
                        R.id.song_format_ArtistName,
                        R.id.song_format_SongImg,
                        songItems1,
                        3, /* add song to playing next & playlist AVAILABLE */
                        context);
                holder.childRecyclerView.setAdapter(mAdapter);
            }
            if(categories.get(position).equals("column 2")) {
                songsListAdapter mAdapter = new songsListAdapter(
                        R.layout.song_format,
                        R.id.song_format_SongName,
                        R.id.song_format_ArtistName,
                        R.id.song_format_SongImg,
                        songItems2,
                        3, /* add song to playing next & playlist AVAILABLE */
                        context);
                holder.childRecyclerView.setAdapter(mAdapter);
            }

        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public RecyclerView childRecyclerView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                childRecyclerView = itemView.findViewById(R.id.recyclerView);
            }
        }
    }
}
