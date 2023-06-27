package com.yolastudio.bilog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Activities.WallpaperDetailActivity;
import com.yolastudio.bilog.Models.Wallpaper;
import com.yolastudio.bilog.R;

import java.util.Collections;
import java.util.List;

public class WallpaperAdapter extends RecyclerView.Adapter<WallpaperAdapter.MyViewHolder> {

    Context mContext;
    List<Wallpaper> mData;

    String PostKey;

    public WallpaperAdapter(Context mContext, List<Wallpaper> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Collections.reverse(mData);
    }

    @NonNull
    @Override
    public WallpaperAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_wallpaper_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperAdapter.MyViewHolder holder, int position) {
        Picasso.get()
                .load(mData.get(position).getPicture())
                .placeholder(R.drawable.one_reeler_izone_text)
                .resize(720, 720)
                .onlyScaleDown()
                .centerInside()
                .error(R.drawable.one_reeler_izone_text)
                .config(Bitmap.Config.RGB_565)
                .into(holder.imgWallpaper);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgWallpaper;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgWallpaper = itemView.findViewById(R.id.row_wallpaper_img);

            imgWallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent wallpaperDetailActivity = new Intent(mContext, WallpaperDetailActivity.class);
                    int position = getAdapterPosition();

                    // get post id
                    PostKey = mData.get(position).getPostKey();

                    wallpaperDetailActivity.putExtra("userId", mData.get(position).getDescription());
                    wallpaperDetailActivity.putExtra("description", mData.get(position).getDescription());
                    wallpaperDetailActivity.putExtra("title", mData.get(position).getTitle());
                    wallpaperDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    wallpaperDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    mContext.startActivity(wallpaperDetailActivity);
                }
            });
        }
    }
}
