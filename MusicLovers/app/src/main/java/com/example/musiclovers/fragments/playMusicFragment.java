package com.example.musiclovers.fragments;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.models.songItem;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class playMusicFragment extends Fragment implements MainActivity.UpdateFragmentPlayMusic {
    String base_Url = "http://10.0.2.2:3000/";
    PlaceHolder placeHolder;
    ImageButton optionButton, btnPause_Start, btnNext, btnPrevious;
    TextView play_music_SongName, play_music_SingerName, play_music_SongEnd, play_music_SongStart;
    SeekBar songSeekBar, volumeSeekBar;
    ImageView play_music_SongImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.play_music, container, false);
        play_music_SongName = v.findViewById(R.id.play_music_SongName);
        play_music_SingerName = v.findViewById(R.id.play_music_SingerName);
        play_music_SongImg = v.findViewById(R.id.play_music_SongImg);
        optionButton = v.findViewById(R.id.play_music_BtnOption);
        play_music_SongEnd = v.findViewById(R.id.play_music_SongEnd);
        play_music_SongStart = v.findViewById(R.id.play_music_SongStart);
        songSeekBar = v.findViewById(R.id.play_music_SeekbarSong);
        btnPause_Start = v.findViewById(R.id.play_music_BtnPlay);
        btnNext = v.findViewById(R.id.play_music_BtnForward);
        btnPrevious = v.findViewById(R.id.play_music_BtnBackward);
        volumeSeekBar = v.findViewById(R.id.play_music_SeekbarVolume);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializeRetrofit();
        handlerBtnOption();
        btnPause_StartHandler();
        btnNext_PreviousHandler();
        volumeSeekBar();
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((MainActivity) getActivity()).mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }
    private void initializeRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
    }

    private void handlerBtnOption() {
        optionButton.setOnClickListener(view -> {
            ArrayList<songItem> songList = ((MainActivity) getContext()).songList;
            int position = ((MainActivity) getContext()).position;
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.inflate(R.menu.play_music_option_menu);
            popup.show();
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.add_to_playlist_PlayMusic:
                        if(!songList.isEmpty()) {
                            ((MainActivity) getContext()).addSongToPlaylist(songList.get(position));
                        }
                        break;
                    case R.id.go_to_artist:
                        if(!songList.isEmpty()){
                            ((MainActivity) getContext()).goToArtistDetail(songList.get(position));
                            ((MainActivity) getContext()).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                    case R.id.show_album:
                        if(!songList.isEmpty()){
                            ((MainActivity) getContext()).goToAlbumDetail(songList.get(position));
                            ((MainActivity) getContext()).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                    case R.id.like:
                        //like song and add song to playlist [favorite songs]
                        if(!songList.isEmpty()){
                            ((MainActivity) getContext()).loveMeOrNot(songList.get(position));
                        }
                        break;
                }
                return true;
            });
        });
    }

    private void btnPause_StartHandler() {
        btnPause_Start.setOnClickListener(v -> {
            if (((MainActivity) getActivity()).mediaPlayer != null) {
                if (((MainActivity) getActivity()).mediaPlayer.isPlaying()) {
                    ((MainActivity) getActivity()).onSongPause();
                } else {
                    ((MainActivity) getActivity()).onSongPlay();
                }
            }
        });
    }

    private void btnNext_PreviousHandler(){
        btnPrevious.setOnClickListener(view -> {
            if(((MainActivity) getActivity()).mediaPlayer != null){
                ((MainActivity) getActivity()).onSongPrevious();
            }
        });
        btnNext.setOnClickListener(view -> {
            if(((MainActivity) getActivity()).mediaPlayer != null){
                ((MainActivity) getActivity()).onSongNext();
            }
        });
    }

    void volumeSeekBar(){
        int stream = AudioManager.STREAM_MUSIC;
        AudioManager audioManager = ((MainActivity) getContext()).audioManager;
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(stream));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(stream));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {}

            public void onStopTrackingTouch(SeekBar bar) {}
        });
    }

    @Override
    public void updateSong(String songName, String artistName, String imgUrl) {
        play_music_SingerName.setText(artistName);
        play_music_SongName.setText(songName);
        new DownloadImageTask((play_music_SongImg)).execute(base_Url + imgUrl);
    }

    @Override
    public void updateBtnPlay(int btn) {
        btnPause_Start.setImageResource(btn);
    }

    @Override
    public void updateSongProgress(int progress) {
        songSeekBar.setProgress(progress);
    }

    @Override
    public void updateSongEnd(String time) {
        play_music_SongEnd.setText(time);
    }

    @Override
    public void updateSongStart(String time) {
        play_music_SongStart.setText(time);
    }

    @Override
    public void updateVolumeProgress(int progress) {
        volumeSeekBar.setProgress(progress);
    }

    @Override
    public void setMaxSongProgress(int max) {
        songSeekBar.setMax(max);
    }
}
