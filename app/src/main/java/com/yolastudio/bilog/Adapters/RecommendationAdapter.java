package com.yolastudio.bilog.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Activities.ExplorePostDetailActivity;
import com.yolastudio.bilog.Models.Post;
import com.yolastudio.bilog.R;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    Context mContext;
    List<Post> mData;

    public RecommendationAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Collections.shuffle(mData);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_rpost_item, parent, false);
        return new RecommendationAdapter.ViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(mData.get(position).getPicture())
                .config(Bitmap.Config.RGB_565)
                .into(holder.imgPost);
        //holder.title.setText(mData.get(position).getTitle());
        holder.desc.setText(mData.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPost;
        TextView title, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.row_rpost_img);
            //title = itemView.findViewById(R.id.rpostTitle);
            desc = itemView.findViewById(R.id.rpostDesc);

            imgPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, ExplorePostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("userId", mData.get(position).getDescription());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    //exploreDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postData", timestamp);
                    mContext.startActivity(postDetailActivity);
                }
            });
        }
    }
}

