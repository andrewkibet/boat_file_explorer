package com.bomboverk.boat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bomboverk.boat.ExplorerActivity;
import com.bomboverk.boat.R;

public class FavsFragment extends Fragment {


    private CardView soundsCard;
    private CardView videosCard;
    private CardView imagesCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favs, container, false);

        soundsCard = view.findViewById(R.id.frag_favs_sounds);
        videosCard = view.findViewById(R.id.frag_favs_videos);
        imagesCard = view.findViewById(R.id.frag_favs_images);

        soundsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExplorerActivity.class);
                intent.putExtra("type", "sounds");
                startActivity(intent);
            }
        });

        videosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExplorerActivity.class);
                intent.putExtra("type", "videos");
                startActivity(intent);
            }
        });

        imagesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ExplorerActivity.class);
                intent.putExtra("type", "images");
                startActivity(intent);
            }
        });

        return view;
    }
}
