package com.kajianid.ustadz.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.ui.main.article.ArticleFragment;
import com.kajianid.ustadz.ui.main.kajian.KajianFragment;
import com.kajianid.ustadz.ui.mosque.MosqueListFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final Context context;

    @StringRes
    private final int[] tabTitles = {
            R.string.tab_article,
            R.string.tab_kajian,
            R.string.tab_mosque
    };

    public MainPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(tabTitles[position]);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ArticleFragment();
                break;
            case 1:
                fragment = new KajianFragment();
                break;
            case 2:
                fragment = new MosqueListFragment();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
