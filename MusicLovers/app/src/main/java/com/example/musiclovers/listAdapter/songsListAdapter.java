package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.models.songItem;

import java.util.ArrayList;

public class songsListAdapter extends RecyclerView.Adapter<songsListAdapter.ViewHolder>{
    int layoutHolder, tvName, tvArtist, ivImage;
    private ArrayList<songItem> songItems;
    private Context context;

    public songsListAdapter(int layoutHolder, int tvName, int tvArtist, int ivImage, ArrayList<songItem> songItems, Context context) {
        this.layoutHolder = layoutHolder;
        this.tvName = tvName;
        this.tvArtist = tvArtist;
        this.ivImage = ivImage;
        this.songItems = songItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutHolder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        songItem currentSong = songItems.get(position);
        String base_url = "http://10.0.2.2:3000/";
        new DownloadImageTask(holder.image).execute(base_url + currentSong.getSongImg());
        holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.artistName.setText(currentSong.getArtistName());
        holder.songName.setText(currentSong.getSongName());
        holder.itemView.setOnClickListener(view -> (
                (MainActivity) context).getSongs(songItems, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return songItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView songName;
        TextView artistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(tvName);
            artistName = itemView.findViewById(tvArtist);
            image = itemView.findViewById(ivImage);
        }
    }
}
