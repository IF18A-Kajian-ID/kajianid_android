package com.kajianid.android.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kajianid.android.activities.ArticleIslamiActivity;
import com.kajianid.android.activities.DataSessionHandler;
import com.kajianid.android.activities.KajianActivity;
import com.kajianid.android.activities.MosqueLocationActivity;
import com.kajianid.android.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.contentFragmentHome.txtNamaUser1.setText(
                Build.MANUFACTURER + " " + Build.MODEL
        );
        binding.btnArticle.setOnClickListener(it -> {
            Intent i = new Intent(getContext(), ArticleIslamiActivity.class);
            startActivity(i);
        });
        binding.btnKajian.setOnClickListener(it ->{
            Intent i = new Intent(getContext(), KajianActivity.class);
            startActivity(i);
        });
        binding.btnMosqueLocation.setOnClickListener(it -> {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=masjid terdekat");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }
}