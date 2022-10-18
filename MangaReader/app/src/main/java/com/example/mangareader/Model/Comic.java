package com.example.mangareader.Model;

import java.util.List;

public class Comic {
    private String Id;
    private String Name;
    private String Image;
    private String Category;
    private List<Chapter> Chapters;
    private int Like;
    private int Favorite;
    private String Summary;

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

    public String getCategory() {
        return Category;
    }

    public List<Chapter> getChapters() {
        return Chapters;
    }

    public int getLike() {
        return Like;
    }

    public int getFavorite() {
        return Favorite;
    }

    public String getSummary() {
        return Summary;
    }

    public void setLike(int like) {
        Like = like;
    }

    public void setFavorite(int favorite) {
        Favorite = favorite;
    }
}
