package com.example.mangareader.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mangareader.Fragment.RankingFavoriteFragment;
import com.example.mangareader.Fragment.RankingLikeFragment;

public class MyViewPager2Adapter extends FragmentStateAdapter {

    public MyViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public MyViewPager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public MyViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new RankingFavoriteFragment();
            case 0:
            default:
                return new RankingLikeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
