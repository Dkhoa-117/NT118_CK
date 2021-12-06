package com.example.musiclovers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
    BottomNavigationView bottomNavigationView;
    private BottomSheetBehavior bottomSheetBehavior;

    //fragment song_tab
    TextView song_tab_SongName;
    ImageView song_tab_SongImg;
    ImageButton song_tab_btnPause_Start, song_tab_btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new listenNowFragment()).commit();

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

    public void getSongs(ArrayList<songItem> songItems, int current) {

        //Đặt một thông báo hỏi user có muốn xoá hàng đợi hay không (trong trường hợp hàng đợi có sẵn)
        //if(true)
        songList.clear();
        position = 0;
        for(int i = current; i < songItems.size(); i++){
            songList.add(songItems.get(i));
        }
        updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
        prepareSongAndPlay();
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

}