package com.example.courseonline.Adapter.Learner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.courseonline.Fragment.Learner.DiscoverFragment;
import com.example.courseonline.Fragment.Learner.HomeFragment;
import com.example.courseonline.Fragment.Learner.LearningFragment;
import com.example.courseonline.Fragment.Learner.ProfileFragment;
import com.example.courseonline.Fragment.Learner.RankingFragment;

public class ViewPage2Adapter extends FragmentStateAdapter {
    public ViewPage2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new HomeFragment();
            case 1: return new DiscoverFragment();
            case 2: return new LearningFragment();
            case 3: return new RankingFragment();
            case 4: return new ProfileFragment();
            default: return new HomeFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
