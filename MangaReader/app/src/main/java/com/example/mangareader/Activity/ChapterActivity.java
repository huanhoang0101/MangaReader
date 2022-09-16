package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyChapterAdapter;
import com.example.mangareader.Adapter.MyComicAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.Model.Favorite;
import com.example.mangareader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChapterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtChapterName;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    Button btnFavorite, btnLike;

    String newFavorites;

    //Firebase Database
    DatabaseReference table_user;

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

        //View Favorite and Like
        ViewFavoriteAndLike();

        //Favorite
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.currentUser.getFavorites().contains(Common.comicSelected.Id))
                {
                    RemoveFavorite();
                }
                else
                {
                    AddFavorite();
                }
            }
        });
    }

    private void ViewFavoriteAndLike() {
        //Favorite
        if(Common.currentUser.getFavorites().contains(Common.comicSelected.Id)){
            btnFavorite.setText("Đã yêu thích");
        }
    }

    private void AnhXa() {
        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");

        txtChapterName = findViewById(R.id.txt_chapName);
        recyclerView = findViewById(R.id.recycler_chapter);
        toolbar = findViewById(R.id.toolbar);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnLike = findViewById(R.id.btn_like);
    }

    private void fetchChapter(Comic comicSelected) {
        Common.chapterList = comicSelected.Chapters;
        recyclerView.setAdapter(new MyChapterAdapter(this, comicSelected.Chapters));
        txtChapterName.setText(new StringBuilder("CHAPTER (")
        .append(comicSelected.Chapters.size())
        .append(")"));
    }

    private void AddFavorite(){
        Map<String, Object> favorites = new HashMap<>();
        newFavorites = Common.currentUser.getFavorites() + "," + Common.comicSelected.Id;
        favorites.put("favorites", newFavorites);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(favorites)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnFavorite.setText("Đã yêu thích");
                        Common.currentUser.setFavorites(newFavorites);
                        Toast.makeText(ChapterActivity.this, "Đã thêm vào Yêu thích", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void RemoveFavorite(){
        String oldFavorites = Common.currentUser.getFavorites();
        String[] arr = oldFavorites.split(",");
        newFavorites = "";
        for (String s: arr) {
            if(s.equals(Common.comicSelected.Id)){
                continue;
            }
            else {
                newFavorites = newFavorites + "," + s;
            }
        }
        newFavorites = newFavorites.substring(1);

        Map<String, Object> favorites = new HashMap<>();
        favorites.put("favorites", newFavorites);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(favorites)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnFavorite.setText("Yêu thích");
                        Common.currentUser.setFavorites(newFavorites);
                        Toast.makeText(ChapterActivity.this, "Đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}