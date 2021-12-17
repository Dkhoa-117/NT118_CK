package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.R;
import com.example.musiclovers.models.playlistItem;
import java.util.ArrayList;
/**
 * DONE
 */
public class playlistAdapter extends ArrayAdapter<playlistItem> {
    private Context context;
    int layoutHolder, tvName, tvNumSongs, ivImage;

    public playlistAdapter(int layoutHolder, int tvName, int tvNumSongs, int ivImage, ArrayList<playlistItem> playlistItems, Context context) {
        super(context, layoutHolder, playlistItems);
        this.context = context;
        this.tvName = tvName;
        this.tvNumSongs = tvNumSongs;
        this.ivImage = ivImage;
        this.layoutHolder = layoutHolder;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layoutHolder, null, false);
        }
        String baseUrl = "http://10.0.2.2:3000/";
        playlistItem playlist = getItem(position);
        TextView playlistName = convertView.findViewById(R.id.playlist_format_Name);
        TextView numSongs = convertView.findViewById(R.id.playlist_format_NumSongs);
        ImageView playlistImg = convertView.findViewById(R.id.playlist_format_Image);
        if(playlist != null){
            new DownloadImageTask(playlistImg).execute(baseUrl + playlist.getPlaylistImg());
            playlistName.setText(playlist.getPlaylistName());
            numSongs.setText(playlist.getNumSongs() + " Songs");
            playlistImg.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        }
        return convertView;
    }
}
