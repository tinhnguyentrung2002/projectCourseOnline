package com.example.courseonline.Adapter.Learner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.courseonline.Fragment.Learner.AllCourseFragment;
import com.example.courseonline.Fragment.Learner.FavouriteFragment;

public class ViewPage2AdapterProfile extends FragmentStateAdapter {
    public ViewPage2AdapterProfile(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new AllCourseFragment();
            case 1: return new FavouriteFragment();
            default: return new AllCourseFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
