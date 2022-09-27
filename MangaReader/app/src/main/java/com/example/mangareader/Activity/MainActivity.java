package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.IComicLoadDone;
import com.example.mangareader.Model.Banner;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
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
    ImageSlider imageSlider;

    //Firebase Database
    DatabaseReference table_comic;
    DatabaseReference table_banner;

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
                loadBanner();
                loadComic();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
                loadComic();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bottomNavigationView.setSelectedItemId(R.id.action_home);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        //Dang o home
                        break;
                    case R.id.action_favorite:
                        if (!Common.Login)
                            ShowDialogLogin();
                        else
                            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
                        break;
                    case R.id.action_category:
                        startActivity(new Intent(MainActivity.this, CategoryActivity.class));
                        break;
                    case R.id.action_user:
                        if (!Common.Login)
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        else
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                }
                return true;
            }
        });
    }

    private void AnhXa() {
        //Init Database
        table_comic = FirebaseDatabase.getInstance().getReference("Comic");
        table_banner = FirebaseDatabase.getInstance().getReference("Banners");

        //Init Listener
        comicListener = this;

        btnFilterSearch = findViewById(R.id.btn_search);
        swipeRefreshLayout = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recycler_comic);
        txtComic = findViewById(R.id.txt_comic);
        bottomNavigationView = findViewById(R.id.menu_nav);
        imageSlider = findViewById(R.id.image_slider);
    }

    private void loadBanner() {
        table_banner.addListenerForSingleValueEvent(new ValueEventListener() {
            List<SlideModel> imageBannerList = new ArrayList<>();
            List<Banner> bannerList = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot bannerSnapshot : snapshot.getChildren()) {
                    Banner banner = bannerSnapshot.getValue(Banner.class);
                    String image = banner.getImage();
                    imageBannerList.add(new SlideModel(image, ScaleTypes.CENTER_CROP));
                    bannerList.add(banner);
                }
                imageSlider.setImageList(imageBannerList);
                imageSlider.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Banner banner = bannerList.get(i);
                        for (Comic comic : Common.comicList) {
                            if (comic.Id.equals(banner.getComic_id())) {
                                Common.comicSelected = comic;
                                Intent intent = new Intent(MainActivity.this, ChapterActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "ERROR" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComic() {
        table_comic.addListenerForSingleValueEvent(new ValueEventListener() {
            List<Comic> comic_load = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot comicSnapshot : snapshot.getChildren()) {
                    Comic comic = comicSnapshot.getValue(Comic.class);
                    comic_load.add(comic);
                }

                comicListener.onComicLoadDoneListener(comic_load);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "ERROR" + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        swipeRefreshLayout.setRefreshing(false);
    }

    private void ShowDialogLogin() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Thông báo!");
        alertDialog.setMessage("Vui lòng đăng nhập để thực hiện chức năng này");

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        alertDialog.show();
    }
}