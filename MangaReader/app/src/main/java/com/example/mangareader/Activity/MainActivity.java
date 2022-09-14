package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.IComicLoadDone;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IComicLoadDone {

    TextView txtComic;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView btnFilterSearch;
    BottomNavigationView bottomNavigationView;

    //Firebase Database
    DatabaseReference comics;

    //Listener
    IComicLoadDone comicListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();

        btnFilterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FilterSearchActivity.class));
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadComic();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadComic();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        bottomNavigationView.inflateMenu(R.menu.main_menu);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        //Dang o Home
                        break;
                    case R.id.action_favorite:
                        //Qua favorite Ac
                        break;
                    case R.id.action_category:
                        //qua category Ac
                        break;
                    case R.id.action_user:
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                }
            }
        });
    }

    private void AnhXa() {
        //Init Database
        comics = FirebaseDatabase.getInstance().getReference("Comic");

        //Init Listener
        comicListener = this;

        btnFilterSearch = findViewById(R.id.btn_search);
        swipeRefreshLayout = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recycler_comic);
        txtComic = findViewById(R.id.txt_comic);
        bottomNavigationView = findViewById(R.id.menu_nav);
    }

    private void loadComic() {
        comics.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Comic> comic_load = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot comicSnapshot: snapshot.getChildren())
                {
                    Comic comic = comicSnapshot.getValue(Comic.class);
                    comic_load.add(comic);
                }

                comicListener.onComicLoadDoneListener(comic_load);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onComicLoadDoneListener(List<Comic> comicList) {
        Common.comicList = comicList;

        recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comicList));

        txtComic.setText(new StringBuilder("NEW COMIC (")
        .append(comicList.size())
        .append(")"));


    }
}