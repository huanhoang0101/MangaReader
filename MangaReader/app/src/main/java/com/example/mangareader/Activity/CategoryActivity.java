package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

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

        bottomNavigationView.inflateMenu(R.menu.main_menu);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        startActivity(new Intent(CategoryActivity.this, MainActivity.class));
                        break;
                    case R.id.action_favorite:
                        if(Common.Login == false) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CategoryActivity.this);
                            alertDialog.setTitle("Thông báo!");
                            alertDialog.setMessage("Vui lòng đăng nhập để thực hiện chức năng này");

                            alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialog.setNegativeButton("Đăng nhập", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                                }
                            });
                            alertDialog.show();
                        }
                        else
                            startActivity(new Intent(CategoryActivity.this, FavoriteActivity.class));
                        break;
                    case R.id.action_category:
                        //Dang o category
                        break;
                    case R.id.action_user:
                        if(Common.Login == false)
                            startActivity(new Intent(CategoryActivity.this, LoginActivity.class));
                        else
                            startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
                        break;
                }
            }
        });
    }

    private void ViewChipCategory() {
        chipAll.setChecked(true);
        recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), Common.comicList));

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
                if (c.Category.contains(chip.getText().toString()))
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
}