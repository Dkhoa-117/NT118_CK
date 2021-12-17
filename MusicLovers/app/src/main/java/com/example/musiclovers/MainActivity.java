package com.example.musiclovers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiclovers.fragments.albumDetailFragment;
import com.example.musiclovers.fragments.artistDetailFragment;
import com.example.musiclovers.fragments.browseFragment;
import com.example.musiclovers.fragments.libraryFragment;
import com.example.musiclovers.fragments.listenNowFragment;
import com.example.musiclovers.fragments.lyricsFragment;
import com.example.musiclovers.fragments.playMusicFragment;
import com.example.musiclovers.fragments.playingNextFragment;
import com.example.musiclovers.fragments.searchFragment;
import com.example.musiclovers.listAdapter.playlistAdapter;
import com.example.musiclovers.models.albumItem;
import com.example.musiclovers.models.artistItem;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;
import com.example.musiclovers.signIn_signUpActivity.SaveSharedPreference;
import com.example.musiclovers.signIn_signUpActivity.loginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Playable{
    /* Declare */
    String base_Url = "http://10.0.2.2:3000/";
    public ArrayList<songItem> songList = new ArrayList<>();
    public MediaPlayer mediaPlayer;
    public Handler handler = new Handler();
    PlaceHolder placeHolder;
    public int position = 0;
    boolean nextSong = false;
    BottomNavigationView bottomNavigationView;
    public BottomSheetBehavior bottomSheetBehavior;
    LinearLayout tab_song_container;
    NotificationManager manager;
    public AudioManager audioManager;
    public boolean isRepeat = false;

    //interface - using to update fragment
    public static UpdateFragmentPlayMusic updateFragmentPlayMusic;
    public static UpdateFragmentPlayingNext updateFragmentPlayingNext;

    //fragment song_tab
    TextView song_tab_SongName;
    ImageView song_tab_SongImg;
    ImageButton song_tab_btnPause_Start, song_tab_btnNext;

    //ViewPager - using to create view that slide left and right
    private ViewPager2 mPager;
    private ScreenSlidePagerAdapter pagerAdapter;
    public static Fragment play_music, playingNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        initializeRetrofit();
        setUpVolumeSeekBar();
        tabSong_playMusicFragment();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new listenNowFragment()).commit();
        NavigationBar();
        createNotificationChannel();
        viewPagerInit();

        btnPause_StartHandler();
        btnNext_PreviousHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logout();
    }

    public void logout(){
        if(SaveSharedPreference.getUserName(this).isEmpty()) {
            Intent login = new Intent(this, loginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onResume() {
        if(updateFragmentPlayMusic != null)
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        super.onResume();
    }

    private void findViewById() {
        tab_song_container = findViewById(R.id.song_tab_container);
        song_tab_SongName = findViewById(R.id.song_tab_SongName);
        song_tab_SongImg = findViewById(R.id.song_tab_SongImg);
        song_tab_btnPause_Start = findViewById(R.id.img_btn_song_tab_Play);
        song_tab_btnNext = findViewById(R.id.img_btn_song_tab_Forward);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mPager = findViewById(R.id.pager);
    }

    private void initializeRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
    }

    private void viewPagerInit(){
        mPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);
        View v =  mPager.getChildAt(0);
        if (v != null) {
            v.setNestedScrollingEnabled(false);
        }
    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                playingNext = new playingNextFragment();
                updateFragmentPlayingNext = (UpdateFragmentPlayingNext) playingNext;
                return playingNext;
            } else if (position == 1) {
                play_music = new playMusicFragment();
                updateFragmentPlayMusic = (UpdateFragmentPlayMusic) play_music;
                return play_music;
            } else {
                return new lyricsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private void tabSong_playMusicFragment() {
        /* Show up play_music bottom sheet */
        tab_song_container.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        bottomSheetBehavior = BottomSheetBehavior.from(mPager);
        /* Show & Hide song_tab */
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.15) {
                    tab_song_container.setVisibility(View.GONE);
                } else if (slideOffset <= 0.15) {
                    tab_song_container.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void goToAlbumDetail(songItem songItem) {
        Call<albumItem> call = placeHolder.getAlbum(songItem.getAlbumId());
        call.enqueue(new Callback<albumItem>() {
            @Override
            public void onResponse(@NonNull Call<albumItem> call, @NonNull Response<albumItem> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                albumItem album = response.body();
                ViewModel viewModel = new ViewModelProvider(MainActivity.this).get(ViewModel.class);
                viewModel.select(album);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new albumDetailFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }

            @Override
            public void onFailure(@NonNull Call<albumItem> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goToArtistDetail(songItem songItem){
        String[] artistId = songItem.getArtistId();
        Call<artistItem> call = placeHolder.getArtist(artistId[0]);
        call.enqueue(new Callback<artistItem>() {
            @Override
            public void onResponse(@NonNull Call<artistItem> call, @NonNull Response<artistItem> response) {
                if(response.isSuccessful()){
                    artistItem artist = response.body();
                    ViewModel viewModel = new ViewModelProvider(MainActivity.this).get(ViewModel.class);
                    viewModel.select(artist);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new artistDetailFragment())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }

            @Override
            public void onFailure(@NonNull Call<artistItem> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getSongs(ArrayList<songItem> songItems, int current) {
        songList.clear();
        position = 0;
        for(int i = current; i < songItems.size(); i++){
            songList.add(songItems.get(i));
        }
        updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
        prepareSongAndPlay();
    }

    public void loveMeOrNot(songItem song) {
        Call<Void> call = placeHolder.likeSong(SaveSharedPreference.getId(this), song.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    boolean loved = response.code() == 201;
                    int ic_image;
                    String message;
                    if(loved){
                        ic_image = R.drawable.ic_fill_heart;
                        message = "Add to Favorite List";
                    }else{
                        ic_image = R.drawable.ic_unfill_heart;
                        message = "Remove from Favorite List";
                    }
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_custom, findViewById(R.id.toast_layout_root));
                    ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
                    image.setImageResource(ic_image);
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(message);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    Toast.makeText(getApplicationContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "code: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSongToPlaylist(songItem song){

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_playlists);
        ListView listView = dialog.findViewById(R.id.dialog_playlist_ListView);
        //get playlists
        Call<List<playlistItem>> call = placeHolder.getPlaylistByUser_PlaylistNum(SaveSharedPreference.getId(this), 2);
        call.enqueue(new Callback<List<playlistItem>>() {
            @Override
            public void onResponse(Call<List<playlistItem>> call, Response<List<playlistItem>> response) {
                if(response.isSuccessful()){
                    ArrayList<playlistItem> playlistItems = (ArrayList<playlistItem>) response.body();
                    if(playlistItems.isEmpty()){
                        Toast.makeText(MainActivity.this, "No playlist to add !! \n Please create a playlist first", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }
                    playlistAdapter adapter = new playlistAdapter(
                            R.layout.playlist_format,
                            R.id.playlist_format_Name,
                            R.id.playlist_format_NumSongs,
                            R.id.playlist_format_Image,
                            playlistItems,
                            getApplicationContext()
                    );
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //add song to playlist
                            Call<Void> addSongToPlaylist = placeHolder.addSongToPlaylist(playlistItems.get(i).get_id(), song.get_id());
                            addSongToPlaylist.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code() == 200){
                                        Toast.makeText(MainActivity.this, "Song added !!", Toast.LENGTH_LONG).show();
                                    }else if(response.code() == 201){
                                        Toast.makeText(MainActivity.this, "Song is Already in the Playlist !!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<playlistItem>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void NavigationBar() {
        //load Listen Now when open app
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    selectedFragment = new listenNowFragment();
                    break;
                case R.id.Browse_page:
                    selectedFragment = new browseFragment();
                    break;
                case R.id.Library_page:
                    selectedFragment = new libraryFragment();
                    break;
                case R.id.Search_page:
                    selectedFragment = new searchFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });
        bottomNavigationView.setOnItemReselectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    selectedFragment = new listenNowFragment();
                    break;
                case R.id.Browse_page:
                    selectedFragment = new browseFragment();
                    break;
                case R.id.Library_page:
                    selectedFragment = new libraryFragment();
                    break;
                case R.id.Search_page:
                    selectedFragment = new searchFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        });
    }

    private void btnPause_StartHandler() {
        song_tab_btnPause_Start.setOnClickListener(view -> {
            if(mediaPlayer != null){
                if (mediaPlayer.isPlaying()) {
                    onSongPause();
                } else {
                    onSongPlay();
                }
            }
        });
    }

    private void btnNext_PreviousHandler() {
        song_tab_btnNext.setOnClickListener(view -> {
            if(mediaPlayer!=null){
                onSongNext();
            }
        });
    }

    private void prepareSongAndPlay() {
        songItem song = songList.get(position);
        song_tab_SongName.setText(song.getSongName() + " - " + song.getArtistName());
        song_tab_SongName.setSelected(true);
        updateFragmentPlayMusic.updateSong(song.getSongName(), song.getArtistName(), song.getSongImg());
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_pause);
        new DownloadImageTask(song_tab_SongImg).execute(base_Url + song.getSongImg());

        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                nextSong = true;
            });
            mediaPlayer.setDataSource(base_Url + song.getSongSrc());
            mediaPlayer.prepare();
            mediaPlayer.start();
            recentlyPlayed(song.get_id());
        } catch (IOException e) {
            e.printStackTrace();
        }

        timerSetting();
        updateSongSeekBar();
        new createNotification(this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_pause,
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());

        handler.postDelayed(playingOrder, 1000);
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_pause);
    }

    void recentlyPlayed(String songId){
        Call<Void> call = placeHolder.addSong2RecentList(SaveSharedPreference.getId(this), songId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void songPlayback() {
        try { mediaPlayer.prepare(); } catch (IOException e) { e.printStackTrace(); }
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_play_music);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_play_music);
        handler.removeCallbacks(update);
        updateFragmentPlayMusic.updateSongProgress(0);
        updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(0));
        new createNotification(this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_play_music,
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());
    }

    private void setUpVolumeSeekBar() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createNotificationChannel() {
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    createNotification.CHANNEL_ID_1,
                    createNotification.CHANNEL_NAME_1,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
            channel.enableLights(true);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public Runnable update = new Runnable() {
        @Override
        public void run() {
            updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(mediaPlayer.getCurrentPosition()));
            updateSongSeekBar();
        }
    };

    Runnable playingOrder = new Runnable() {
        @Override
        public void run() {
            if (nextSong) {
                position++;
                if(isRepeat){
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        handler.removeCallbacks(update);
                        updateFragmentPlayMusic.updateSongProgress(0);
                        updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    updateFragmentPlayingNext.updateDisplayNextSongsList(true, false, null);
                    if (position == (songList.size())) {
                        position = 0;
                        //nếu mảng bài hát chỉ gồm 1 phần tử thì chỉ cần prepare không cần tải lại
                        if (songList.size() == 1) {
                            songPlayback();
                        } else {
                            mediaPlayer.reset();
                            prepareSongAndPlay();
                            updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
                            updateFragmentPlayMusic.updateSongProgress(0);
                            onSongPause();
                        }
                    } else {
                        mediaPlayer.reset();
                        prepareSongAndPlay();
                    }
                }
                nextSong = false;
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case createNotification.ACTION_PREVIOUS:
                    onSongPrevious();
                    break;
                case createNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()){
                        onSongPause();
                    } else {
                        onSongPlay();
                    }
                    break;
                case createNotification.ACTION_NEXT:
                    onSongNext();
                    break;
            }
        }
    };

    @Override
    public void onSongPrevious() {
        if(position == 0 || mediaPlayer.getCurrentPosition() > 5000) {
            mediaPlayer.stop();
            try { mediaPlayer.prepare(); } catch (IOException e) { e.printStackTrace(); }
            mediaPlayer.start();
        }
        else {

            updateFragmentPlayingNext.updateDisplayNextSongsList(false, true, songList.get(position));
            position--;
            prepareSongAndPlay();
        }
    }

    @Override
    public void onSongPlay() {
        mediaPlayer.start();
        updateSongSeekBar();
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_pause);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_pause);
        new createNotification(MainActivity.this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_pause, /* switch to pause */
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());
        updateSongSeekBar();
    }

    @Override
    public void onSongPause() {
        handler.removeCallbacks(update);
        mediaPlayer.pause();
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_play_music);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_play_music);
        new createNotification(MainActivity.this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_play_music,
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());
    }

    @Override
    public void onSongNext() {
        if (position == songList.size() - 1) {
            position = 0;
            if(songList.size() == 1) {
                mediaPlayer.stop();
                songPlayback();
            }
            else {
                mediaPlayer.reset();
                prepareSongAndPlay();
                updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
                updateFragmentPlayMusic.updateSongProgress(0);
                onSongPause();
            }
        }
        else {
            updateFragmentPlayingNext.updateDisplayNextSongsList(true, false, null);
            position++;
            prepareSongAndPlay();
        }
    }

    private void timerSetting() {
        updateFragmentPlayMusic.updateSongEnd(new SimpleDateFormat("mm:ss").format(mediaPlayer.getDuration()));
        updateFragmentPlayMusic.setMaxSongProgress(mediaPlayer.getDuration());
    }

    private void updateSongSeekBar() {
        if (mediaPlayer.isPlaying()) {
            updateFragmentPlayMusic.updateSongProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(update,300);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }

    public interface UpdateFragmentPlayMusic{
        void updateSong(String songName, String artistName, String imgUrl);
        void updateSongProgress(int progress);
        void updateBtnPlay(int btn);
        void updateSongEnd(String time);
        void updateSongStart(String time);
        void updateVolumeProgress(int progress);
        void setMaxSongProgress(int max);
    }

    public interface UpdateFragmentPlayingNext{
        void initDisplayNextSongsList(ArrayList<songItem> songsList, int position);
        void updateDisplayNextSongsList(Boolean isNext, Boolean isPrevious, songItem song);
    }
}
