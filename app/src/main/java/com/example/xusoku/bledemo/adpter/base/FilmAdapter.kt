package com.example.xusoku.bledemo.adpter.base

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.nekocode.kotgo.sample.presentation.gotoPage2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.example.xusoku.bledemo.Grils
import com.example.xusoku.bledemo.R
import com.example.xusoku.bledemo.util.CommonManager

/**
 * Created by xusoku on 2016/4/7.
 */
class FilmAdapter(activity: Activity) : ExRecyclerAdapter<Grils.Gril, FilmAdapter.ViewHolder>(activity) {

    private val mMaxImageHeight = 1000
    private val mMinImageHeight = 150
    var mImageWidth: Float = 0.0f


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        mImageWidth = (CommonManager.DISPLAY_METRICS.widthPixels - CommonManager.dpToPx(30f)) / 2f
        return ViewHolder(inflateView(R.layout.item_recyc_layout, parent))
    }


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {

        holder?.bindView(data[position], position)
    }


    inner class ViewHolder(view: View) : ExRecyclerAdapter.ExViewHolder<Grils.Gril>(view) {
        override fun bindView(gril: Grils.Gril, pos: Int) {

            val iv = findView(R.id.item_iv) as ImageView

            val tv = findView(R.id.item_tv) as TextView


            val img = "http://tnfs.tngou.net/img" + gril.img;


            if(gril.height!=-1){
                val params = iv.getLayoutParams()
                params.height = gril.height
                iv.setLayoutParams(params)
                Glide.with(activity)
                        .load(img)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(iv)
            }else {
                Glide.with(activity)
                        .load(img)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_loading)
                        .dontAnimate()
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
                                //                    if (iv.tag != null && iv.tag.equals(img)) {

                                val width = resource?.getWidth() as Int
                                val height = resource?.height as Int
                                //                                        Log.e("123===="+gril.id,"width-"+width+"   height-"+height);
                                var imageHeight = (mImageWidth * 1.0f / width * height).toInt()
                                if (imageHeight > mMaxImageHeight) {
                                    imageHeight = mMaxImageHeight
                                }
                                if (imageHeight < mMinImageHeight) {
                                    imageHeight = mMinImageHeight
                                }
                                val params = iv.getLayoutParams()
                                params.height = imageHeight

                                gril.height = imageHeight
                                iv.setLayoutParams(params)
                                iv.setImageBitmap(resource)
                            }
                        })
            }
            tv.text = gril?.title
            iv.setOnClickListener {
                activity.gotoPage2("http://tnfs.tngou.net/img" + gril.img)
            }
        }

    }
}
