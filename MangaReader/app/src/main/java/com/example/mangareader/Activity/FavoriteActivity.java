package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;

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

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        fetchFavorite();

        bottomNavigationView.inflateMenu(R.menu.main_menu);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
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
                        if(Common.Login == false)
                            startActivity(new Intent(FavoriteActivity.this, LoginActivity.class));
                        else
                            startActivity(new Intent(FavoriteActivity.this, ProfileActivity.class));
                        break;
                }
            }
        });


    }

    private void AnhXa() {
        recyclerView = findViewById(R.id.recycler_favorite);
        bottomNavigationView = findViewById(R.id.bottom_nav);
    }

    private void fetchFavorite() {
        List<Comic> comic_favorite = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Id != null) {
                if (Common.currentUser.getFavorites().contains(c.Id))
                    comic_favorite.add(c);
            }
        }
        if(comic_favorite.size() > 0)
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_favorite));
        else {
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_favorite));
            Toast.makeText(this, "Bạn chưa yêu thích truyện nào", Toast.LENGTH_SHORT).show();
        }
    }
}