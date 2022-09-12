package com.example.mangareader.Adapter;

import android.widget.ImageView;

import java.util.List;

public class BannerAdapter {
    private List<String> imageList;

    public BannerAdapter(List<String> imageList) {
        this.imageList = imageList;
    }

    public int getItemCount() {
        return imageList.size();
    }

    public void onBindImageSlide(int position){

    }
}
