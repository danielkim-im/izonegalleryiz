package com.yolastudio.bilog.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.yolastudio.bilog.Activities.PostDetailActivity;
import com.yolastudio.bilog.Models.Post;
import com.yolastudio.bilog.R;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;

    FirebaseDatabase firebaseDatabase;
    FirebaseStorage mStorage;
    DatabaseReference databaseReference;
    String PostKey;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Collections.reverse(mData);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item, parent, false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        Picasso.get()
                .load(mData.get(position).getPicture())
                .config(Bitmap.Config.RGB_565)
                .into(holder.imgPost);

        Glide.with(mContext).load(R.drawable.one_reeler_logo).into(holder.imgPostProfile);
    }

    @Override
    public int getItemCount() {
        //return mData.size();
        return 12;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        PhotoView imgPost;
        ImageView imgPostProfile, commentBtn, shareBtn, downloadBtn, showContentBtn;
        RecyclerView recyclerView;
        EditText editTextComment;

        public MyViewHolder(final View itemView){
            super(itemView);

            imgPost = itemView.findViewById(R.id.row_post_img);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile);
            recyclerView = itemView.findViewById(R.id.postRV);
            editTextComment = itemView.findViewById(R.id.cafe_comment_edittext);
            commentBtn = itemView.findViewById(R.id.row_post_comment);
            shareBtn = itemView.findViewById(R.id.row_post_share);
            showContentBtn = itemView.findViewById(R.id.showPostContentBtn);
            downloadBtn = itemView.findViewById(R.id.downloadBtnPostItem);
            firebaseDatabase = FirebaseDatabase.getInstance();
            mStorage = FirebaseStorage.getInstance();
            databaseReference = firebaseDatabase.getReference("Posts");

            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    // get post id
                    PostKey = mData.get(position).getPostKey();

                    postDetailActivity.putExtra("userId", mData.get(position).getDescription());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postData", timestamp);
                    postDetailActivity.putExtra("downloadImg", true);
                    mContext.startActivity(postDetailActivity);
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });

            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("userId", mData.get(position).getDescription());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("title", mData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage", mData.get(position).getPicture());
                    postDetailActivity.putExtra("description", mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey", mData.get(position).getPostKey());
                    //postDetailActivity.putExtra("userPhoto", mData.get(position).getUserPhoto());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postData", timestamp);
                    postDetailActivity.putExtra("commenting", true);
                    mContext.startActivity(postDetailActivity);
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    String postTitle = mData.get(position).getTitle();

                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = "Please download IZONE Gallery*IZ to explore the posts.\n\nGoogle Play:\nhttps://play.google.com/store/apps/details?id=com.yolastudio.bilog";
                    String shareSubject = "IZONE GALLERY*IZ " + postTitle;
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    mContext.startActivity(Intent.createChooser(myIntent, "Share " + postTitle));
                }
            });

            showContentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog1);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);

                    ImageView postImgView = bottomSheetDialog.findViewById(R.id.popup_post_img);
                    TextView postDesc = bottomSheetDialog.findViewById(R.id.popupPostContent);

                    //Glide.with(mContext).load(mData.get(position).getPicture()).into(postImgView);
                    Picasso.get()
                            .load(mData.get(position).getPicture())
                            .config(Bitmap.Config.RGB_565)
                            .into(postImgView);
                    postDesc.setText(mData.get(position).getDescription());

                    bottomSheetDialog.show();
                }
            });
        }
    }
    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}