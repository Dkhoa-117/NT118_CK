package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.Playable;
import com.example.musiclovers.R;
import com.example.musiclovers.fragments.playingNextFragment;
import com.example.musiclovers.fragments.playlistDetailFragment;
import com.example.musiclovers.fragments.playlistsFragment;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * DONE
 */
public class songsListAdapter extends RecyclerView.Adapter<songsListAdapter.ViewHolder>{
    int layoutHolder, tvName, tvArtist, ivImage;
    public int clickMode;
    private final ArrayList<songItem> songItems;
    private Context context;

    String base_Url = "http://10.0.2.2:3000/";
    public Fragment base;

    public songsListAdapter(int layoutHolder, int tvName, int tvArtist, int ivImage, ArrayList<songItem> songItems, int clickMode, Context context) {
        this.layoutHolder = layoutHolder;
        this.tvName = tvName;
        this.tvArtist = tvArtist;
        this.ivImage = ivImage;
        this.songItems = songItems;
        this.clickMode = clickMode;
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
        if(currentSong.get_id() != null){
            new DownloadImageTask(holder.image).execute(base_Url + currentSong.getSongImg());
            holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        }else{
            FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
            retriever.setDataSource(currentSong.getSongImg());
            byte [] data = retriever.getEmbeddedPicture();
            if (data != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                holder.image.setImageBitmap(bitmap);
            }
            retriever.release();
        }
        holder.artistName.setText(currentSong.getArtistName());
        holder.songName.setText(currentSong.getSongName());
        holder.itemView.setOnClickListener(view -> ((MainActivity) context).getSongs(songItems, holder.getAdapterPosition()));
        /**
         * ------------------
         * REMOVE_PLAYLIST = 1
         * REMOVE_PLAYING_NEXT = 2
         * ADD2PLAYING_NEXT / ADD2PLAYLIST / LIKE / GO TO ARTIST = 3 (let user choose what they want)
         */
        holder.itemView.setOnLongClickListener(view -> {
            if(clickMode == 1){
                removeFromPlaylist(
                        songItems.get(position).get_id(),
                        ((playlistDetailFragment) base).playlist.get_id()
                );
                songItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, songItems.size());
            }else if(clickMode == 2){
                ((MainActivity) context).songList.remove(songItems.get(position));
                songItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, songItems.size());
            }else if(clickMode == 3){
                PopupMenu popup = new PopupMenu(context, view, Gravity.END);
                popup.getMenuInflater().inflate(R.menu.long_click_song_option_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.song_opt_add_to_playlist){
                        ((MainActivity) context).addSongToPlaylist(songItems.get(position));
                    }else if(item.getItemId() == R.id.song_opt_play_next){
                        ((MainActivity) context).songList.add(songItems.get(position));
                        ((playingNextFragment) MainActivity.playingNext).nextSongs.add(songItems.get(position));
                        ((playingNextFragment) MainActivity.playingNext).adapter.notifyDataSetChanged();
                        Toast.makeText(context, "Add Song To Playing Next", Toast.LENGTH_SHORT).show();
                    }else if(item.getItemId() == R.id.song_opt_like){
                        ((MainActivity) context).loveMeOrNot(songItems.get(position));
                    }else if(item.getItemId() == R.id.song_opt_go_to_artist){
                        ((MainActivity) context).goToArtistDetail(songItems.get(position));
                    }else if(item.getItemId() == R.id.song_opt_go_to_album){
                        ((MainActivity) context).goToAlbumDetail(songItems.get(position));
                    }
                    return true;
                });
                popup.show();
            }
            return true;
        });
    }

    void removeFromPlaylist(String songId, String playlistId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
        Call<Void> call = placeHolder.removeSongInPlaylist(playlistId, songId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "Removed Song", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
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
