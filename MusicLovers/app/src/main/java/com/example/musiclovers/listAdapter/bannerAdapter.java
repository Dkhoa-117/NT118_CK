package com.example.musiclovers.listAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.example.musiclovers.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.ViewModel;
import com.example.musiclovers.fragments.playlistDetailFragment;
import com.example.musiclovers.models.playlistItem;

import java.util.ArrayList;

public class bannerAdapter extends PagerAdapter {
    Context context;
    ArrayList<playlistItem> bannerList;

    public bannerAdapter(Context context, ArrayList<playlistItem> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    //định hình và gán dữ liệu cho mỗi object tượng trưng cho mỗi page
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view= inflater.inflate(R.layout.banner_format,null);

        ImageView backgroundImage = view.findViewById(R.id.banner_format_background_image);
        ImageView bannerImage = view.findViewById(R.id.banner_format_banner_image);
        TextView bannerThumbnail = view.findViewById(R.id.banner_format_thumbnail);
        TextView bannerDescription = view.findViewById(R.id.banner_format_description);

        new DownloadImageTask((ImageView) backgroundImage).execute("http://10.0.2.2:3000/"+ bannerList.get(position).getPlaylistImg());
        new DownloadImageTask(bannerImage).execute("http://10.0.2.2:3000/"+ bannerList.get(position).getPlaylistImg());
        bannerThumbnail.setText(bannerList.get(position).getPlaylistName());
        //bannerDescription.setText(arrayListBanner.get(position).getNoidung());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewModel viewModel = new ViewModelProvider((MainActivity)context).get(ViewModel.class);
                viewModel.select(bannerList.get(position));
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new playlistDetailFragment())//
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
