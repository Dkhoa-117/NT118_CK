package com.example.musiclovers.fragments;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.R;
import com.example.musiclovers.listAdapter.categoriesListAdapter;

import java.util.ArrayList;

/**
 * DONE
 */
public class browseFragment extends Fragment {
    RecyclerView.LayoutManager parentLayoutManager;
    ArrayList<String> categories = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //animation
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categories.add("New Music");
        categories.add("Best New Songs"); //add to test the form of Browser Fragment
        categories.add("New Albums");
        categories.add("Today Top Hits");
        RecyclerView parentRecyclerView = view.findViewById(R.id.parentRecycleView);
        parentRecyclerView.setHasFixedSize(true);
        parentLayoutManager = new LinearLayoutManager(getContext());
        categoriesListAdapter categoriesListAdapter = new categoriesListAdapter(categories, getActivity());
        parentRecyclerView.setLayoutManager(parentLayoutManager);
        parentRecyclerView.setAdapter(categoriesListAdapter);
        categoriesListAdapter.notifyDataSetChanged();
    }
}
