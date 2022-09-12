package com.example.mangareader;

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

public class ChapterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtChapterName;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        //View
        txtChapterName = findViewById(R.id.txt_chapName);
        recyclerView = findViewById(R.id.recycler_chapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        Toolbar toolbar = findViewById(R.id.toolbar);
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

    private void fetchChapter(Comic comicSelected) {
        Common.chapterList = comicSelected.Chapters;
        recyclerView.setAdapter(new MyChapterAdapter(this, comicSelected.Chapters));
        txtChapterName.setText(new StringBuilder("CHAPTER (")
        .append(comicSelected.Chapters.size())
        .append(")"));
    }
}