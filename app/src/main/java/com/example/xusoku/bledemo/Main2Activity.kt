package com.example.xusoku.bledemo

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cn.kejin.android.views.XImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.example.xusoku.bledemo.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main2.*
import org.jetbrains.anko.act

class Main2Activity : BaseActivity() {
    override fun setLayoutView(): Int {
        return R.layout.activity_main2
    }

    var pic : String = ""

    override fun initVariable() {
        pic=intent.getStringExtra("gril")
    }

    override fun findViews() {
    }

    override fun initData() {
        Glide.with(act)
                .load(pic)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
//        .into(big_iv)
                .into(object : SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {

                         big_iv.setImage(resource)

                    }
                })
    }

    override fun setListener() {
    }

    override fun doClick(view: View?) {
    }

}
