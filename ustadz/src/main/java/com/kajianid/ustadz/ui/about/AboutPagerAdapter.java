package com.kajianid.ustadz.ui.about;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kajianid.ustadz.R;

public class AboutPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    @StringRes
    private final int[] tabTitles = {
            R.string.leader,
            R.string.member_one,
            R.string.member_two,
            R.string.member_three,
            R.string.member_four
    };

    public AboutPagerAdapter(Context context, FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return DevelopersFragment.newInstance(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(tabTitles[position]);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
