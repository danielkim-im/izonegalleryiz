package com.yolastudio.bilog.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yolastudio.bilog.Models.CafeComment;
import com.yolastudio.bilog.R;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OpenChatAdapter extends RecyclerView.Adapter<OpenChatAdapter.CafeCommentViewHolder> {

    private Context mContext;
    private List<CafeComment> mData;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public OpenChatAdapter(Context mContext, List<CafeComment> mData) {
        this.mContext = mContext;
        this.mData = mData;
        Collections.reverse(mData);
    }

    @NonNull
    @Override
    public CafeCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            View row1 = LayoutInflater.from(mContext).inflate(R.layout.row_cafe_comment_me, parent, false);
            return new CafeCommentViewHolder(row1);
        }else{
            View row2 = LayoutInflater.from(mContext).inflate(R.layout.row_cafe_comment_other, parent, false);
            return new CafeCommentViewHolder(row2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CafeCommentViewHolder holder, int position) {
        //Glide.with(mContext).load(mData.get(position).getUimg()).into(holder.img_user);
        holder.tv_name.setText(mData.get(position).getUname());
        holder.tv_content.setText(mData.get(position).getContent());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));

        if (mData.get(position).getUid().equals("YLDvbvS0sbYtQE4bZiSbD2saLWQ2")){
            holder.adminSignImg.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(R.drawable.one_reeler_logo).into(holder.img_user);
        }else {
            holder.adminSignImg.setVisibility(View.GONE);
            Glide.with(mContext)
                    .load(mData.get(position).getUimg())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(holder.img_user);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CafeCommentViewHolder extends RecyclerView.ViewHolder{

        ImageView adminSignImg, img_user;
        TextView tv_name, tv_date, tv_content;

        public CafeCommentViewHolder(View itemView){
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("CafeComment");

            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
            adminSignImg = itemView.findViewById(R.id.adminSignImageComment);

            Paper.init(mContext);
            updateView((String) Paper.book().read("language"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(mData.get(position).getUid().equals(currentUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("a h:mm", calendar).toString();
        return date;
    }

    private void updateView(String lang){
        Context context = LocaleHelper.setLocale(mContext, lang);
        Resources resources = context.getResources();
    }
}