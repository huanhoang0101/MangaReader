package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Adapter.MyChapterAdapter;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ChapterActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtChapterName;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    Button btnFavorite, btnLike;
    ImageView imgBanner;
    TextView txtFavorite, txtLike, txtSummary;
    ChipGroup chipGroup;

    String newFavorites;
    String newLike;

    //Firebase Database
    DatabaseReference table_user;
    DatabaseReference table_comic;

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

        LoadInfoComic();
        fetchChapter(Common.comicSelected);

        //View Favorite and Like
        if (Common.Login)
            ViewFavoriteAndLike();

        //Favorite
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.Login) {
                    if (Common.currentUser.getFavorites().contains(Common.comicSelected.Id)) {
                        RemoveFavorite();
                    } else {
                        AddFavorite();
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChapterActivity.this);
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
                            startActivity(new Intent(ChapterActivity.this, LoginActivity.class));
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        //Like
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.Login) {
                    if (Common.currentUser.getLike().contains(Common.comicSelected.Id)) {
                        RemoveLike();
                    } else {
                        AddLike();
                    }
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChapterActivity.this);
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
                            startActivity(new Intent(ChapterActivity.this, LoginActivity.class));
                        }
                    });
                    alertDialog.show();
                }
            }
        });
    }

    private void ViewFavoriteAndLike() {
        //Favorite
        if (Common.currentUser.getFavorites().contains(Common.comicSelected.Id)) {
            btnFavorite.setText("Đã theo dõi");
        }
        //Like
        if (Common.currentUser.getLike().contains(Common.comicSelected.Id)) {
            btnLike.setText("Đã Like");
        }
    }

    private void AnhXa() {
        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");
        table_comic = FirebaseDatabase.getInstance().getReference("Comic");

        txtChapterName = findViewById(R.id.txt_chapName);
        recyclerView = findViewById(R.id.recycler_chapter);
        toolbar = findViewById(R.id.toolbar);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnLike = findViewById(R.id.btn_like);
        imgBanner = findViewById(R.id.img_banner_chapter);
        txtFavorite = findViewById(R.id.txt_favorite);
        txtLike = findViewById(R.id.txt_like);
        chipGroup = findViewById(R.id.chip_group_chapter);
        txtSummary = findViewById(R.id.txt_summary);
    }

    private void fetchChapter(Comic comicSelected) {
        Common.chapterList = comicSelected.Chapters;
        recyclerView.setAdapter(new MyChapterAdapter(this, comicSelected.Chapters));
        txtChapterName.setText(new StringBuilder("CHAPTER (")
                .append(comicSelected.Chapters.size())
                .append(")"));
    }

    private void AddFavorite() {
        Map<String, Object> favorites = new HashMap<>();
        if (Common.currentUser.getFavorites().length() == 0)
            newFavorites = Common.currentUser.getFavorites() + Common.comicSelected.Id;
        else
            newFavorites = Common.currentUser.getFavorites() + "," + Common.comicSelected.Id;
        favorites.put("favorites", newFavorites);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(favorites)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnFavorite.setText("Đã theo dõi");
                        Common.currentUser.setFavorites(newFavorites);
                        Common.comicSelected.Favorite = Common.comicSelected.Favorite + 1;
                        UpdateFavoriteComic();
                        Toast.makeText(ChapterActivity.this, "Đã thêm vào danh sách theo dõi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void RemoveFavorite() {
        String oldFavorites = Common.currentUser.getFavorites();
        String[] arr = oldFavorites.split(",");
        newFavorites = "";
        for (String s : arr) {
            if (s.equals(Common.comicSelected.Id)) {
                continue;
            } else {
                newFavorites = newFavorites + "," + s;
            }
        }
        if (newFavorites.length() > 0)
            newFavorites = newFavorites.substring(1);

        Map<String, Object> favorites = new HashMap<>();
        favorites.put("favorites", newFavorites);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(favorites)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnFavorite.setText("Theo dõi");
                        Common.currentUser.setFavorites(newFavorites);
                        Common.comicSelected.Favorite = Common.comicSelected.Favorite - 1;
                        UpdateFavoriteComic();
                        Toast.makeText(ChapterActivity.this, "Đã xóa khỏi danh sách theo dõi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddLike() {
        Map<String, Object> likes = new HashMap<>();
        if (Common.currentUser.getLike().length() == 0)
            newLike = Common.currentUser.getLike() + Common.comicSelected.Id;
        else
            newLike = Common.currentUser.getLike() + "," + Common.comicSelected.Id;
        likes.put("like", newLike);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(likes)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnLike.setText("Đã Like");
                        Common.currentUser.setLike(newLike);
                        Common.comicSelected.Like = Common.comicSelected.Like + 1;
                        UpdateLikeComic();
                        Toast.makeText(ChapterActivity.this, "Đã Like truyện này", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void RemoveLike() {
        String oldLike = Common.currentUser.getLike();
        String[] arr = oldLike.split(",");
        newLike = "";
        for (String s : arr) {
            if (s.equals(Common.comicSelected.Id)) {
                continue;
            } else {
                newLike = newLike + "," + s;
            }
        }
        if (newLike.length() > 0)
            newLike = newLike.substring(1);

        Map<String, Object> likes = new HashMap<>();
        likes.put("like", newLike);

        table_user.child(Common.currentUser.getUserName())
                .updateChildren(likes)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btnLike.setText("Like");
                        Common.currentUser.setLike(newLike);
                        Common.comicSelected.Like = Common.comicSelected.Like - 1;
                        UpdateLikeComic();
                        Toast.makeText(ChapterActivity.this, "Đã hủy Like", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UpdateLikeComic() {
        Map<String, Object> like = new HashMap<>();
        int Likes = Common.comicSelected.Like;
        like.put("Like", Likes);

        table_comic.child(Common.comicSelected.Id)
                .updateChildren(like)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void UpdateFavoriteComic() {
        Map<String, Object> favorite = new HashMap<>();
        int favorites = Common.comicSelected.Favorite;
        favorite.put("Favorite", favorites);

        table_comic.child(Common.comicSelected.Id)
                .updateChildren(favorite)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChapterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void LoadInfoComic() {
        Picasso.get().load(Common.comicSelected.Image).into(imgBanner);

        txtFavorite.setText(String.valueOf(Common.comicSelected.Favorite));
        txtLike.setText(String.valueOf(Common.comicSelected.Like));

        String category = Common.comicSelected.Category;
        String[] arr = category.split(",");

        for (String s : arr) {
            Chip chip = new Chip(this);
            chip.setText(s);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.ChipCategoryClicked = s;
                    startActivity(new Intent(ChapterActivity.this, CategoryActivity.class));
                }
            });
            chipGroup.addView(chip);
        }

        txtSummary.setText(Common.comicSelected.Summary);
    }
}