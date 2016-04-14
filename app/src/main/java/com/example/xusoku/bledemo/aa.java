package com.example.xusoku.bledemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.example.xusoku.bledemo.adpter.base.CommonFragmentAdapter;
import com.example.xusoku.bledemo.util.CommonManager;

/**
 * Created by xusoku on 2016/4/5.
 */
public class aa  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        CommonFragmentAdapter aa= new CommonFragmentAdapter(getSupportFragmentManager(), null);

        int width=123;
        int height=123;
       int mImageWidth=123;
        int imageHeight = (int) (mImageWidth * 1.0f / width * height);


    }
}
