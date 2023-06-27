package com.yolastudio.bilog.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ExplorePostAdapter extends RecyclerView.Adapter<ExplorePostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage mStorage;
    DatabaseReference databaseReference;
    Boolean isConnected;
    String PostKey;

    public ExplorePostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Collections.reverse(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_search_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get()
                .load(mData.get(position).getPicture())
                .placeholder(R.drawable.one_reeler_izone_text)
                .config(Bitmap.Config.RGB_565)
                .into(holder.imgPost);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imgPost;
        RecyclerView recyclerView;

        public MyViewHolder(final View itemView){
            super(itemView);

            imgPost = itemView.findViewById(R.id.row_search_img);
            recyclerView = itemView.findViewById(R.id.searchRV);

            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            mStorage = FirebaseStorage.getInstance();
            databaseReference = firebaseDatabase.getReference("Posts");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent exploreDetailActivity = new Intent(mContext, ExplorePostDetailActivity.class);
                    int position = getAdapterPosition();

                    exploreDetailActivity.putExtra("userId", mData.get(position).getDescription());
                    exploreDetailActivity.putExtra("description", mData.get(position).getDescription());
                    exploreDetailActivity.putExtra("title", mData.get(position).getTitle());
                    exploreDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    exploreDetailActivity.putExtra("description", mData.get(position).getDescription());
                    exploreDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    //exploreDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    exploreDetailActivity.putExtra("postData", timestamp);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, imgPost, ViewCompat.getTransitionName(imgPost));
                    mContext.startActivity(exploreDetailActivity, optionsCompat.toBundle());
                }
            });
        }
    }

    public void checkConnection(){
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if(null != activeNetwork){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                isConnected = true;
            }

            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                isConnected = true;
            }
        }
        else{
            showMessage("No Internet Connection");
            isConnected = false;
        }
    }



    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}