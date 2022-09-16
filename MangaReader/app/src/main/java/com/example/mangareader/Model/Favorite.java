package com.example.mangareader.Model;

import java.util.List;

public class Favorite {
    private List<String> comicId;

    public Favorite(List<String> comicId) {
        this.comicId = comicId;
    }

    public List<String> getComicId() {
        return comicId;
    }

    public void setComicId(List<String> comicId) {
        this.comicId = comicId;
    }
}
