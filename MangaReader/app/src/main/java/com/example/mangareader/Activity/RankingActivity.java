package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mangareader.Adapter.MyViewPager2Adapter;
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.R;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import io.paperdb.Paper;

public class RankingActivity extends AppCompatActivity implements ILanguage {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    Toolbar toolbar;
    TextView txtTitleToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        AnhXa();

        updateView(Paper.book().read("language"));

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
                        tab.setText(getString(R.string.ranking_by_like));
                        break;
                    case 1:
                        tab.setText(getString(R.string.ranking_by_follow));
                        break;
                }
            }
        }).attach();
    }

    private void AnhXa() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager2);
        toolbar = findViewById(R.id.toolbar);
        txtTitleToolbar = findViewById(R.id.txt_title_toolbar);
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        txtTitleToolbar.setText(resources.getString(R.string.ranking_title_toolbar));
    }
}