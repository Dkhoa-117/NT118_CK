package com.example.musiclovers.fragments;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.ViewModel;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.models.songItem;
import com.example.musiclovers.listAdapter.songsListAdapter;
import com.example.musiclovers.signIn_signUpActivity.SaveSharedPreference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
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
public class libraryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private songsListAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //playlist
        ConstraintLayout playlist = view.findViewById(R.id.fragment_library_Playlist);
        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new playlistsFragment())
                        .addToBackStack("null")
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        //artist
        ConstraintLayout artist = view.findViewById(R.id.fragment_library_Artist);
        artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new artistsFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        //genre
        ConstraintLayout genre = view.findViewById(R.id.fragment_library_Genre);
        genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new genresFragment())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_library_AllSongs);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        runtimePermission();
    }
    public void runtimePermission(){
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    public ArrayList<File> findSong(File file) {
        ArrayList songsSrc = new ArrayList();
        File [] files = file.listFiles();
        if(files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    songsSrc.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".wav")) {
                        songsSrc.add(singleFile);
                    } else if (singleFile.getName().endsWith(".mp3")) {
                        songsSrc.add(singleFile);
                    }
                }

            }
        }
        return songsSrc;
    }
    void displaySongs(){
        ArrayList<songItem> songList = new ArrayList<>();
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
        String[] items = new String[mySongs.size()];
        for(int i = 0; i < mySongs.size(); i++){
            items[i] = mySongs.get(i).getName()
                    .toString()
                    .replace(".mp3", "")
                    .replace("wav", "");
            String[] name = items[i].split(" - ");
            String artistName;
            if(name.length == 1){
               artistName = "Unknown Artist";
            }else{
                artistName = name[1];
            }
            songItem song = new songItem(null, name[0], artistName, null,
                    null, null, 0, mySongs.get(i).toString(), mySongs.get(i).toString());
            songList.add(song);
        }
        //call adapter to display
        mAdapter = new songsListAdapter(
                R.layout.song_format,
                R.id.song_format_SongName,
                R.id.song_format_ArtistName,
                R.id.song_format_SongImg,
                songList,
                3,
                getContext());
        mRecyclerView.setAdapter(mAdapter);
    }
}
