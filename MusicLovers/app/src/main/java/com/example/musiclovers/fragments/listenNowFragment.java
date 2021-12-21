package com.example.musiclovers.fragments;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.bannerAdapter;
import com.example.musiclovers.listAdapter.categoriesListAdapter;
import com.example.musiclovers.models.playlistItem;
import com.example.musiclovers.signIn_signUpActivity.SaveSharedPreference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class listenNowFragment extends Fragment {
    CircleImageView CImgProfile;
    Dialog dialog;
    RecyclerView.LayoutManager parentLayoutManager;
    ArrayList<String> categories = new ArrayList<>();
    Runnable runnable;
    Handler handler;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
        categories.add("Recently Played");
        categories.add("Your Favorite");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_now, container, false);
        CImgProfile = view.findViewById(R.id.listen_now_profile);
        new DownloadImageTask(CImgProfile).execute("http://10.0.2.2:3000/"+SaveSharedPreference.getAvatar(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CImgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        if(savedInstanceState == null){
            RecyclerView parentRecyclerView = view.findViewById(R.id.listen_now_parentRecycleView);
            parentRecyclerView.setHasFixedSize(true);
            parentLayoutManager = new LinearLayoutManager(getContext());
            categoriesListAdapter categoriesListAdapter = new categoriesListAdapter(categories, getActivity());
            parentRecyclerView.setLayoutManager(parentLayoutManager);
            parentRecyclerView.setAdapter(categoriesListAdapter);
            //categoriesListAdapter.notifyDataSetChanged();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
        ViewPager banner = view.findViewById(R.id.banner);
        CircleIndicator circleIndicator = view.findViewById(R.id.banner_indicator);
        Call<List<playlistItem>> call = placeHolder.getPlaylistByUser_PlaylistNum("61bf9959d2b2d206fd981469", 2);
        call.enqueue(new Callback<List<playlistItem>>() {
            @Override
            public void onResponse(Call<List<playlistItem>> call, Response<List<playlistItem>> response) {
                if(response.isSuccessful()){
                    ArrayList<playlistItem> Banners = (ArrayList<playlistItem>) response.body();
                    bannerAdapter bannerAdapter = new bannerAdapter(getActivity(), Banners);
                    banner.setAdapter(bannerAdapter);
                    circleIndicator.setViewPager(banner);

                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            int currentItem = banner.getCurrentItem();
                            currentItem++;
                            if(currentItem>=banner.getAdapter().getCount()){
                                currentItem = 0;
                            }
                            banner.setCurrentItem(currentItem,true);
                            handler.postDelayed(runnable,5500);
                        }
                    };
                    handler.postDelayed(runnable,5500);
                }
            }

            @Override
            public void onFailure(Call<List<playlistItem>> call, Throwable t) {

            }
        });
    }

    private void showDialog(){
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_user_info);
        View v = getView();
        TextView tvUserName = dialog.findViewById(R.id.user_info_username_value);
        TextView tvEmail = dialog.findViewById(R.id.user_info_email_value);
        ImageView ivAvatar = dialog.findViewById(R.id.user_info_avatar);
        tvUserName.setText(SaveSharedPreference.getUserName(getContext()));
        tvEmail.setText(SaveSharedPreference.getEmailAddress(getContext()));
        String base_url = "http://10.0.2.2:3000/";
        new DownloadImageTask(ivAvatar).execute(base_url+SaveSharedPreference.getAvatar(getContext()));
        Button btnSignOut;
        btnSignOut = dialog.findViewById(R.id.user_info_btn_sign_out);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                SaveSharedPreference.clearUser(getContext());
                ((MainActivity) getContext()).logout();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
