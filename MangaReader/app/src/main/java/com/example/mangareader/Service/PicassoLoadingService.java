package com.example.mangareader.Service;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PicassoLoadingService {
    public void loadImage(String url, ImageView imageView){
        Picasso.get().load(url).into(imageView);
    }

    public void loadImage(int resource, ImageView imageView){
        Picasso.get().load(resource).into(imageView);
    }

    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}
