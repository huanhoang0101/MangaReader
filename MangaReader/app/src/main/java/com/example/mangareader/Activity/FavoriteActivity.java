package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.Interface.IMenu;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class FavoriteActivity extends AppCompatActivity implements IMenu, ILanguage {

    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;
    TextView txtFavoriteTitle;

    @Override
    protected void onRestart() {
        super.onRestart();

        fetchFavorite();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        AnhXa();

        updateView(Paper.book().read("language"));

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        fetchFavorite();

        bottomNavigationView.setSelectedItemId(R.id.action_favorite);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setMenu(item);
                return true;
            }
        });
    }

    private void AnhXa() {
        recyclerView = findViewById(R.id.recycler_favorite);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        txtFavoriteTitle = findViewById(R.id.favorite_title);
    }

    private void fetchFavorite() {
        List<Comic> comic_favorite = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Id != null) {
                if (Common.currentUser.getFavorites().contains(c.Id))
                    comic_favorite.add(c);
            }
        }
        recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_favorite));
        if(comic_favorite.size() == 0)
            Toast.makeText(this, getString(R.string.no_comic_follow), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void setMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(FavoriteActivity.this, MainActivity.class));
                break;
            case R.id.action_favorite:
                //Dang o favorite
                break;
            case R.id.action_category:
                startActivity(new Intent(FavoriteActivity.this, CategoryActivity.class));
                break;
            case R.id.action_user:
                if(!Common.Login)
                    startActivity(new Intent(FavoriteActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(FavoriteActivity.this, ProfileActivity.class));
                break;
        }
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        txtFavoriteTitle.setText(resources.getString(R.string.FAVORITE));
    }
}