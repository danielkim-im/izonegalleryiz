package com.yolastudio.bilog.Adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class SaveImageHelper implements Target {

    private Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
    private WeakReference<ContentResolver> contentResolverWeakReference;
    private String name;
    private String desc;

    public static boolean imgDownloaded;

    public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver contentResolver, String name, String desc) {
        this.context = context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.contentResolverWeakReference = new WeakReference<ContentResolver>(contentResolver);
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        ContentResolver r = contentResolverWeakReference.get();
        AlertDialog dialog = alertDialogWeakReference.get();

        if(r != null){
            MediaStore.Images.Media.insertImage(r, bitmap, name, desc);
            imgDownloaded = true;
        }
        dialog.dismiss();
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        AlertDialog dialog = alertDialogWeakReference.get();
        dialog.setMessage("Unexpected Error");
        imgDownloaded = false;
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }
}