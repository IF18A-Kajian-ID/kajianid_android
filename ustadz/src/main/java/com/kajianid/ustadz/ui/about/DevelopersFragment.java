package com.kajianid.ustadz.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.databinding.FragmentDevelopersBinding;
import com.kajianid.ustadz.utils.DeveloperData;


public class DevelopersFragment extends Fragment {

    private static final String ARG_DEVELOPER_POSITION = "arg_developer_position";
    private FragmentDevelopersBinding binding;

    public DevelopersFragment() {
        // Required empty public constructor
    }

    public static DevelopersFragment newInstance(int position) {
        DevelopersFragment fragment = new DevelopersFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_DEVELOPER_POSITION, position);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDevelopersBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int position = 1;
        if (getArguments() != null) {
            position = requireArguments().getInt(ARG_DEVELOPER_POSITION, 1);
        }

        Glide.with(this)
                .load(DeveloperData.drawableId[position])
                .override(150, 150)
                .centerCrop()
                .into(binding.civPhoto);

        binding.tvName.setText(DeveloperData.name[position]);
        binding.tvNim.setText(DeveloperData.nim[position]);
        binding.tvGrade.setText(DeveloperData.kelas[position]);
        binding.tvYearRegistered.setText(DeveloperData.yearRegister[position]);
        binding.tvGitHub.setText(DeveloperData.githubUsername[position]);
        binding.tvEmail.setText(DeveloperData.email[position]);
        binding.tvAddress.setText(DeveloperData.address[position]);

        String githubUrl = "https://github.com" + DeveloperData.githubUsername[position];
        binding.btnVisit.setOnClickListener(itView -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(githubUrl));
            startActivity(i);
        });
    }
}