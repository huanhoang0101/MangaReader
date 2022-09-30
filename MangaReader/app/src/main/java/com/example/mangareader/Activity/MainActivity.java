package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.Interface.IMenu;
import com.example.mangareader.Model.Banner;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.example.mangareader.TouchDetectableScrollView;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements IComicLoadDone, IMenu, ILanguage {

    TextView txtComic;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView btnFilterSearch;
    BottomNavigationView bottomNavigationView;
    ImageSlider imageSlider;
    TouchDetectableScrollView touchDetectableScrollView;

    private boolean isLoading;
    private boolean isLastPage;
    private int totalPage;
    private int currentPage = 1;
    private List<Comic> comicList;

    //Firebase Database
    DatabaseReference table_comic;
    DatabaseReference table_banner;

    //Listener
    IComicLoadDone comicListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "vi-rVN"));
    }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        updateView(Paper.book().read("language"));
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();

        Paper.init(this);

        //Set default language VN
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language", "vi-rVN");

        updateView(Paper.book().read("language"));

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
                currentPage = 1;
                isLastPage = false;
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
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setMenu(item);
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
        txtComic = findViewById(R.id.txt_new_comic);
        bottomNavigationView = findViewById(R.id.menu_nav);
        imageSlider = findViewById(R.id.image_slider);
        touchDetectableScrollView = findViewById(R.id.touchDetectableScrollView);
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        //txtComic.setText(resources.getString(R.string.new_comic));
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
                Common.comicList = comic_load;
                comicListener.onComicLoadDoneListener();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "ERROR" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onComicLoadDoneListener() {
        totalPage = ((Common.comicList.size() - 1) / 5) + 1;
        comicList = new ArrayList<>();
        comicList = getListComic();
        MyComicAdapter adapter = new MyComicAdapter(getBaseContext(), comicList);
        recyclerView.setAdapter(adapter);

        if(currentPage < totalPage) {
            adapter.addFooterLoading();
        } else {
            isLastPage = true;
        }

        touchDetectableScrollView.setMyScrollChangeListener(new TouchDetectableScrollView.OnMyScrollChangeListener() {
            @Override
            public void onBottomReached() {
                if(isLoading || isLastPage){
                    return;
                }
                isLoading = true;
                currentPage += 1;

                loadNextPage(adapter);
            }
        });
        setCountNewComic();
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void setMenu(MenuItem item) {
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
    }

    private List<Comic> getListComic() {
        List<Comic> list = new ArrayList<>();

        if(currentPage < totalPage) {
            for (int i = 0; i < 5; i++) {
                int position = (currentPage - 1) * 5;
                list.add(Common.comicList.get(position + i));
            }
        }
        else {
            for (int position = (currentPage - 1) * 5; position < (Common.comicList.size()); position++){
                list.add(Common.comicList.get(position));
            }
        }
        Toast.makeText(MainActivity.this, "getlist",Toast.LENGTH_SHORT).show();
        return list;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadNextPage(MyComicAdapter adapter) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Comic> list = getListComic();
                adapter.removeFooterLoading();
                comicList.addAll(list);
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Loadnext",Toast.LENGTH_SHORT).show();

                isLoading = false;
                if(currentPage < totalPage) {
                    adapter.addFooterLoading();
                } else {
                    isLastPage = true;
                }
                setCountNewComic();
            }
        }, 200);
    }

    private void setCountNewComic(){
        Context context = LocaleHelper.setLocale(this,Paper.book().read("language"));
        Resources resources =  context.getResources();
        if(isLastPage){
            txtComic.setText(new StringBuilder(resources.getString(R.string.new_comic))
                    .append(" (")
                    .append(comicList.size())
                    .append(")"));
        } else {
            txtComic.setText(new StringBuilder(resources.getString(R.string.new_comic))
                    .append(" (")
                    .append(comicList.size() - 1)
                    .append(")"));
        }
    }
}