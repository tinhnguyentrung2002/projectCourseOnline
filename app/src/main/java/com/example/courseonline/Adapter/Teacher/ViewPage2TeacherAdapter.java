package com.example.courseonline.Adapter.Teacher;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.courseonline.Fragment.Teacher.DashboardFragment;
import com.example.courseonline.Fragment.Teacher.ProfileTeacherFragment;
import com.example.courseonline.Fragment.Teacher.SearchFragment;

public class ViewPage2TeacherAdapter extends FragmentStateAdapter{
    public ViewPage2TeacherAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new DashboardFragment();
            case 1: return new SearchFragment();
//            case 2: return new AnalyticsFragment();
            case 3: return new ProfileTeacherFragment();
            default: return new DashboardFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}