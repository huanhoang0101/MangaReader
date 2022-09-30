package com.example.mangareader.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mangareader.Activity.ChapterActivity;
import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.IRecyclerItemClickListener;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyComicAdapter extends RecyclerView.Adapter<MyComicAdapter.MyViewHolder> {

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_LOADING = 2;
    private boolean isLoadingAdd;

    Context context;
    List<Comic> comicList;
    LayoutInflater inflater;

    public MyComicAdapter(Context context, List<Comic> comicList) {
        this.context = context;
        this.comicList = comicList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if((comicList != null) && (position == comicList.size() - 1) && isLoadingAdd){
            return TYPE_LOADING;
        }
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(TYPE_ITEM == viewType) {
            View itemView = inflater.inflate(R.layout.item_comic, parent, false);
            return new MyViewHolder(itemView);
        }
        else {
            View itemView = inflater.inflate(R.layout.item_loading, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(holder.getItemViewType() == TYPE_ITEM) {
            Picasso.get().load(comicList.get(position).Image).into(holder.comicImage);
            if (comicList.get(position).Name.length() <= 25)
                holder.comicName.setText(comicList.get(position).Name);
            else
                holder.comicName.setText(comicList.get(position).Name.substring(0, 22) + "...");
            holder.txtFavorite.setText(String.valueOf(comicList.get(position).Favorite));
            holder.txtLike.setText(String.valueOf(comicList.get(position).Like));
            holder.txtCategory.setText("Thể loại: " + comicList.get(position).Category);

            //Event
            holder.setRecyclerItemClickListener(new IRecyclerItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //Save comic selected
                    Common.comicSelected = comicList.get(position);
                    Intent intent = new Intent(context, ChapterActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView comicName;
        ImageView comicImage;
        TextView txtFavorite, txtLike, txtCategory;
        ProgressBar progressBar;

        IRecyclerItemClickListener recyclerItemClickListener;

        public void setRecyclerItemClickListener(IRecyclerItemClickListener recyclerItemClickListener) {
            this.recyclerItemClickListener = recyclerItemClickListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            comicImage = itemView.findViewById(R.id.image_comic);
            comicName = itemView.findViewById(R.id.txt_comicName);
            txtCategory = itemView.findViewById(R.id.txt_category);
            txtFavorite = itemView.findViewById(R.id.txt_favorite);
            txtLike = itemView.findViewById(R.id.txt_like);
            progressBar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerItemClickListener.onClick(view, getAdapterPosition());
        }
    }

    public void addFooterLoading() {
        isLoadingAdd = true;
        comicList.add(new Comic());
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;

        int position = comicList.size() - 1;
        Comic comic = comicList.get(position);
        if(comic != null) {
            comicList.remove(position);
            notifyItemRemoved(position);
        }
    }

}
