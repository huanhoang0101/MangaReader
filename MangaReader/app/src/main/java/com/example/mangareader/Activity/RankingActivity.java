package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.example.mangareader.Adapter.MyViewPager2Adapter;
import com.example.mangareader.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RankingActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);
        toolbar = findViewById(R.id.toolbar);

        //Set icon
        toolbar.setNavigationIcon(R.drawable.ic_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MyViewPager2Adapter adapter = new MyViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Xếp hạng theo lượt Like");
                        break;
                    case 1:
                        tab.setText("Xếp hạng theo lượt Theo dõi");
                        break;
                }
            }
        }).attach();
    }
}