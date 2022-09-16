package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSearchActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        AnhXa();

        toolbar.setTitle(Common.comicSelected.Name);

        //Set icon
        toolbar.setNavigationIcon(R.drawable.ic_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        bottomNavigationView.inflateMenu(R.menu.filter_search_menu);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_filter:
                        showFilterDialog();
                        break;
                    case R.id.action_search:
                        showSearchDialog();
                        break;
                }
            }
        });
    }

    private void AnhXa() {
        recyclerView = findViewById(R.id.recycler_filter_search);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        toolbar = findViewById(R.id.toolbar);
    }

    private void showSearchDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("TÌM KIẾM");

        LayoutInflater inflater = this.getLayoutInflater();
        View search_layout = inflater.inflate(R.layout.dialog_search, null);

        EditText edtSearch = search_layout.findViewById(R.id.edt_search);

        alertDialog.setView(search_layout);
        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setNegativeButton("TÌM KIẾM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fetchSearch(edtSearch.getText().toString().toLowerCase());
            }
        });
        alertDialog.show();
    }

    private void fetchSearch(String query) {
        List<Comic> comic_search = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Name.toLowerCase().contains(query))
                comic_search.add(c);
        }
        if(comic_search.size() > 0)
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_search));
        else
            Toast.makeText(this,"Không có kết quả", Toast.LENGTH_SHORT).show();
    }

    private void showFilterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilterSearchActivity.this);
        alertDialog.setTitle("CHỌN THỂ LOẠI");

        LayoutInflater inflater = this.getLayoutInflater();
        View filter_layout = inflater.inflate(R.layout.dialog_options, null);

        AutoCompleteTextView txtCategory = filter_layout.findViewById(R.id.txt_category);
        ChipGroup chipGroup = filter_layout.findViewById(R.id.chip_group);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, Common.categories);
        txtCategory.setAdapter(adapter);
        txtCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtCategory.setText("");

                Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
                chip.setText(((TextView) view).getText());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroup.removeView(view);
                    }
                });
                chipGroup.addView(chip);
            }
        });

        alertDialog.setView(filter_layout);
        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                List<String> filter_key = new ArrayList<>();
                StringBuilder filter_query = new StringBuilder("");
                for (int j = 0; j < chipGroup.getChildCount(); j++) {
                    Chip chip = (Chip) chipGroup.getChildAt(j);
                    filter_key.add(chip.getText().toString());
                }
                Collections.sort(filter_key);
                for (String key : filter_key) {
                    filter_query.append(key).append(",");
                }
                filter_query.setLength(filter_query.length() - 1);

                fetchFilterCategory(filter_query.toString());
            }
        });
        alertDialog.show();
    }

    private void fetchFilterCategory(String query) {
        List<Comic> comic_filtered = new ArrayList<>();
        for (Comic c : Common.comicList) {
            if (c.Category != null) {
                if (c.Category.contains(query))
                    comic_filtered.add(c);
            }
        }
        if(comic_filtered.size() > 0)
            recyclerView.setAdapter(new MyComicAdapter(getBaseContext(), comic_filtered));
        else
            Toast.makeText(this,"Không có kết quả", Toast.LENGTH_SHORT).show();
    }
}