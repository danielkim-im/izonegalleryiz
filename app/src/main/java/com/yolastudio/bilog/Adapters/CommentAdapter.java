package com.yolastudio.bilog.Adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.yolastudio.bilog.Activities.HomeActivity;
import com.yolastudio.bilog.Models.Comment;
import com.yolastudio.bilog.Models.Post;
import com.yolastudio.bilog.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment, parent, false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
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

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img_user;
        ImageView adminSignImg;
        TextView tv_name, tv_content, tv_date;

        public CommentViewHolder(View itemView){
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_user_img);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_content = itemView.findViewById(R.id.post_comment_content);
            tv_date = itemView.findViewById(R.id.comment_date);
            adminSignImg = itemView.findViewById(R.id.adminSignImageComment);
        }
    }

    private String timestampToString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm a", calendar).toString();
        return date;
    }
}
