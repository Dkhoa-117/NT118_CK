package com.example.musiclovers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private BottomSheetBehavior bottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout play_music_layout = findViewById(R.id.play_music);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomSheetBehavior = BottomSheetBehavior.from(play_music_layout);
        LinearLayout tab_song_container = findViewById(R.id.song_tab_container);
        //
        tab_song_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        //ẩn/hiện thanh song_tab
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if(slideOffset > 0.3){
                    tab_song_container.setVisibility(View.INVISIBLE);
                }
                else if(slideOffset <= 0.3){
                    tab_song_container.setVisibility(View.VISIBLE);
                }
            }
        });

        //Chuyển thanh bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    //chuyển cảnh
                    return true;
                case R.id.Browse_page:
                   //chuyển cảnh
                    return true;
                case R.id.Library_page:
                    //chuyển cảnh
                    return true;
                case R.id.Search_page:
                    //chuyển cảnh
                    return true;
            }
            return false;
        });
        bottomNavigationView.setOnItemReselectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    //chuyển cảnh
                case R.id.Browse_page:
                    //chuyển cảnh
                case R.id.Library_page:
                    //chuyển cảnh
                case R.id.Search_page:
                    //chuyển cảnh
            }
        });
    }

}