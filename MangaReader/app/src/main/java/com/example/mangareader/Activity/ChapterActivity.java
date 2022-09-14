package com.example.mangareader.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mangareader.Adapter.MyChapterAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;

public class ChapterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtChapterName;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        AnhXa();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        toolbar.setTitle(Common.comicSelected.Name);

        //Set icon
        toolbar.setNavigationIcon(R.drawable.ic_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetchChapter(Common.comicSelected);
    }

    private void AnhXa() {
        //View
        txtChapterName = findViewById(R.id.txt_chapName);
        recyclerView = findViewById(R.id.recycler_chapter);

        toolbar = findViewById(R.id.toolbar);
    }

    private void fetchChapter(Comic comicSelected) {
        Common.chapterList = comicSelected.Chapters;
        recyclerView.setAdapter(new MyChapterAdapter(this, comicSelected.Chapters));
        txtChapterName.setText(new StringBuilder("CHAPTER (")
        .append(comicSelected.Chapters.size())
        .append(")"));
    }
}