package com.example.mangareader.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.mangareader.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {

    Context context;
    List<String> imgLink;
    LayoutInflater inflater;

    public MyViewPagerAdapter(Context context, List<String> imgLink) {
        this.context = context;
        this.imgLink = imgLink;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imgLink.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View image_layout = inflater.inflate(R.layout.item_view_pager, container, false);

        PhotoView pageImage = image_layout.findViewById(R.id.page_image);
        Picasso.get().load(imgLink.get(position)).into(pageImage);

        container.addView(image_layout);
        return image_layout;
    }
}
