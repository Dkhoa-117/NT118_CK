package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.ViewModel;
import com.example.musiclovers.fragments.playlistsFragment;
import com.example.musiclovers.models.playlistItem;

import java.util.ArrayList;

public class playlistAdapterRecyclerView extends RecyclerView.Adapter<playlistAdapterRecyclerView.ViewHolder> {

    private ViewModel viewModel;
    int layoutHolder, tvName, tvNumSongs, ivImage;
    private ArrayList<playlistItem> playlistItems;
    private Context context;

    public playlistAdapterRecyclerView(int layoutHolder, int tvName, int tvNumSongs, int ivImage, ArrayList<playlistItem> playlistItems, Context context) {
        this.layoutHolder = layoutHolder;
        this.tvName = tvName;
        this.tvNumSongs = tvNumSongs;
        this.ivImage = ivImage;
        this.playlistItems = playlistItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutHolder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        playlistItem currentPlaylist = playlistItems.get(position);
        String base_url = "http://10.0.2.2:3000/";
        new DownloadImageTask(holder.image).execute(base_url + currentPlaylist.getPlaylistImg());
        holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.numSongs.setText(""+currentPlaylist.getNumSongs());
        holder.playlistName.setText(currentPlaylist.getPlaylistName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel = new ViewModelProvider((MainActivity)context).get(ViewModel.class);
                viewModel.select(currentPlaylist);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new playlistsFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlistItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView playlistName;
        TextView numSongs;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.playlistName = itemView.findViewById(tvName);
            this.numSongs = itemView.findViewById(tvNumSongs);
            this.image = itemView.findViewById(ivImage);
        }
    }
}
