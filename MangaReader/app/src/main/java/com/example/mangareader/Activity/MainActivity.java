package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.example.mangareader.Service.AlarmReceiver;
import com.example.mangareader.TouchDetectableScrollView;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IComicLoadDone, IMenu, ILanguage {

    TextView txtComic;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    BottomNavigationView bottomNavigationView;
    ImageSlider imageSlider;
    TouchDetectableScrollView touchDetectableScrollView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    ProgressDialog dialog;

    boolean isLoading;
    boolean isLastPage;
    int totalPage;
    int currentPage = 1;
    List<Comic> comicList;

    //Firebase Database
    DatabaseReference table_comic;
    DatabaseReference table_banner;

    //Listener
    IComicLoadDone comicListener;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "vi-rVN"));
    }

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

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

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

        setDailyNotification();
    }

    private void AnhXa() {
        //Init Database
        table_comic = FirebaseDatabase.getInstance().getReference("Comic");
        table_banner = FirebaseDatabase.getInstance().getReference("Banners");

        //Init Listener
        comicListener = this;

        swipeRefreshLayout = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.recycler_comic);
        txtComic = findViewById(R.id.txt_new_comic);
        bottomNavigationView = findViewById(R.id.menu_nav);
        imageSlider = findViewById(R.id.image_slider);
        touchDetectableScrollView = findViewById(R.id.touchDetectableScrollView);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        txtComic.setText(resources.getString(R.string.new_comic));
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
                            if (comic.getId().equals(banner.getComic_id())) {
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
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage(getString(R.string.please_wait_dialog));
        dialog.show();

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

        if(!swipeRefreshLayout.isRefreshing())
            dialog.dismiss();
    }

    private void ShowDialogLogin() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getString(R.string.notice));
        alertDialog.setMessage(getString(R.string.please_login));

        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton(getString(R.string.login), new DialogInterface.OnClickListener() {
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, FilterSearchActivity.class));
                break;
            case R.id.action_ranking:
                startActivity(new Intent(MainActivity.this, RankingActivity.class));
                break;
            case R.id.action_change_language:
                ChangeLanguage();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ChangeLanguage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View languageChange_layout = inflater.inflate(R.layout.dialog_language_change, null);

        RadioButton rdVN = languageChange_layout.findViewById(R.id.rdb_VN);
        RadioButton rdEN = languageChange_layout.findViewById(R.id.rdb_EN);
        RadioButton rdJP = languageChange_layout.findViewById(R.id.rdb_JP);

        String currentLanguage = (String) Paper.book().read("language");

        //Set radio button = current language
        if(currentLanguage.equals("vi")){
            rdVN.setChecked(true);
            currentLanguage = "vi";
        } else if(currentLanguage.equals("en")) {
            rdEN.setChecked(true);
            currentLanguage = "en";
        } else if(currentLanguage.equals("ja")) {
            rdJP.setChecked(true);
            currentLanguage = "ja";
        }

        alertDialog.setView(languageChange_layout);

        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        String finalCurrentLanguage = currentLanguage;
        alertDialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //setLanguageChecked(radioGroup);
                if(rdVN.isChecked() && (!finalCurrentLanguage.equals("vi"))){
                    Paper.book().write("language", "vi");
                    ShowDialogRestart();
                }
                if(rdEN.isChecked() && (!finalCurrentLanguage.equals("en"))) {
                    Paper.book().write("language", "en");
                    ShowDialogRestart();
                }
                if(rdJP.isChecked() && (!finalCurrentLanguage.equals("ja"))) {
                    Paper.book().write("language", "ja");
                    ShowDialogRestart();
                }
            }
        });
        alertDialog.show();
    }

    private void ShowDialogRestart() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(getString(R.string.notice));
        alertDialog.setMessage(getString(R.string.restart));
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RestartApp();
            }
        });
        alertDialog.show();
    }

    private void RestartApp() {
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 1000,
                getIntent(), PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        System.exit(0);
    }

    private void setDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.HOUR_OF_DAY, 0);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}