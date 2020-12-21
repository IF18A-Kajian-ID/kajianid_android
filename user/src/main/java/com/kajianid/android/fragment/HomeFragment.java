package com.kajianid.android.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kajianid.android.DataSessionHandler;
import com.kajianid.android.KajianActivity;
import com.kajianid.android.R;
import com.kajianid.android.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private DataSessionHandler session = null;
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
        session = getActivity().getIntent().getParcelableExtra("session");
        binding.contentFragmentHome.txtNamaUser1.setText(
                (session.getNama_lengkap().equals(null)) ? "" : session.getNama_lengkap()
        );
        binding.btnArticle.setOnClickListener(it -> {
                    Intent i = new Intent(getContext(), KajianActivity.class);
                    startActivity(i);
                });
        binding.btnKajian.setOnClickListener(it ->{
            Intent i =new Intent(getContext(), KajianActivity.class);
            startActivity(i);
        });
    }
}