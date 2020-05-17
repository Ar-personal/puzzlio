package com.example.puzzlio;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class FragmentPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
    public FragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new SocialTab();
            case 1:
                return new PuzzleList();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
