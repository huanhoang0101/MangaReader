package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity implements IMenu,ILanguage {

    Chip chipAll;
    ChipGroup chipGroup;
    RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        AnhXa();

        chipAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), Common.comicList));
            }
        });

        ViewChipCategory();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bottomNavigationView.setSelectedItemId(R.id.action_category);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setMenu(item);
                return true;
            }
        });
    }

    private void ViewChipCategory() {
        if(Common.ChipCategoryClicked.equals("")) {
            chipAll.setChecked(true);
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), Common.comicList));
        }
        else {
            SearchCategoryChip();
        }

        LayoutInflater inflater = getLayoutInflater();
        for(String s: Common.categories){
            Chip chip =  new Chip(this);
            chip = (Chip) inflater.inflate(R.layout.item_chip_category, chipGroup, false);
            chip.setText(s);
            Chip finalChip = chip;
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchCategory(finalChip);
                }
            });
            chipGroup.addView(chip);
        }
    }

    private void AnhXa() {
        chipAll = findViewById(R.id.chip_all);
        chipGroup = findViewById(R.id.chip_group_category);
        recyclerView = findViewById(R.id.recycler_category);
        bottomNavigationView = findViewById(R.id.bottom_nav);
    }

    private void fetchCategory(Chip chip) {
        List<Comic> comic_category = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Category != null) {
                if (c.Category.toLowerCase().contains(chip.getText().toString().toLowerCase(Locale.ROOT)))
                    comic_category.add(c);
            }
        }
        if(comic_category.size() > 0) {
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_category));
        }
        else {
            comic_category.clear();
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_category));
            Toast.makeText(this, "Chưa có truyện thuộc thể loại này", Toast.LENGTH_SHORT).show();
        }
    }

    private void SearchCategoryChip(){
        List<Comic> comic_category = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Category != null) {
                if (c.Category.contains(Common.ChipCategoryClicked))
                    comic_category.add(c);
            }
        }
        recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_category));
        Common.ChipCategoryClicked = "";
    }

    private void ShowDialogLogin(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CategoryActivity.this);
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
                startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
            }
        });
        alertDialog.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void setMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                break;
            case R.id.action_favorite:
                if(!Common.Login)
                    ShowDialogLogin();
                else
                    startActivity(new Intent(CategoryActivity.this, FavoriteActivity.class));
                break;
            case R.id.action_category:
                //Dang o category
                break;
            case R.id.action_user:
                if(!Common.Login)
                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
                break;
        }
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

//        txtComic.setText(resources.getString(R.string.new_comic));
    }
}