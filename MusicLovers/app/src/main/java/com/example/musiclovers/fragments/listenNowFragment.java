package com.example.musiclovers.fragments;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.signIn_signUpActivity.SaveSharedPreference;

import de.hdodenhof.circleimageview.CircleImageView;

public class listenNowFragment extends Fragment {
    CircleImageView CImgProfile;
    Dialog dialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
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
