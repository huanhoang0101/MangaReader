package com.example.mangareader.Interface;

import com.example.mangareader.Model.Comic;

import java.util.List;

public interface IComicLoadDone {
    void onComicLoadDoneListener(List<Comic> comicList);
}
