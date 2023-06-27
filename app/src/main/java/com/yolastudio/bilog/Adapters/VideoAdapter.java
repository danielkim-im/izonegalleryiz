package com.yolastudio.bilog.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Activities.VideoPlayerActivity;
import com.yolastudio.bilog.Models.Video;
import com.yolastudio.bilog.R;

import java.util.Collections;
import java.util.List;

public class VideoAdapter extends PagerAdapter {

    Context context;
    List<Video> videoList;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        Collections.reverse(videoList);
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_video_item, container, false);

        ImageView thumbnailImg = view.findViewById(R.id.thumbImgView);
        TextView vidTitle = view.findViewById(R.id.vidTitle);

        Video video = videoList.get(position);
        String postKey = video.getPostKey();
        final String videoID = video.getVideoID();
        String thumbnail = video.getThumbnail();
        String name = video.getName();

        Picasso.get()
                .load(thumbnail)
                .config(Bitmap.Config.RGB_565)
                .into(thumbnailImg);
        vidTitle.setText(video.getName());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent storyPlayerActivity = new Intent(context, VideoPlayerActivity.class);

                storyPlayerActivity.putExtra("videoID", videoID);
                context.startActivity(storyPlayerActivity);
                if (context instanceof Activity) {
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        ((ViewPager)container).addView(view, ((ViewPager)container).getChildCount() > position ? position : ((ViewPager)container).getChildCount());
        //container.addView(view, position);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}