package com.example.xusoku.bledemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import cn.nekocode.kotgo.sample.presentation.gotoPage3
import com.example.xusoku.bledemo.adpter.base.CommonFragmentAdapter
import com.example.xusoku.bledemo.base.BaseActivity
import com.example.xusoku.bledemo.views.HackyViewPager
import com.example.xusoku.bledemo.views.viewpagerindicator.PageIndicator
import com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar.BitmapBar
import com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar.ColorBar
import com.example.xusoku.bledemo.views.viewpagerindicator.scrollbar.ScrollBar
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main3.*
import java.util.*

class MainActivity : BaseActivity() {

    val tabDrawables = intArrayOf(R.drawable.main_btn_home_selector, R.drawable.main_btn_home_selector, R.drawable.main_btn_home_selector, R.drawable.main_btn_film_review_selector, R.drawable.main_btn_price_selector, R.drawable.main_btn_share_ticket_selector)
    val tabNames = arrayOf("性感", "韩日", "丝袜", "写真", "清纯", "车模")

    override fun setLayoutView(): Int {
        return R.layout.activity_main3
    }

    override fun initVariable() {
    }


    override fun findViews() {
        //        viewPager.toggleLock()
        viewPager.setOffscreenPageLimit(6)
    }

    override fun initData() {
        val mPriceFragment = SampleFragment.newInstance(1);
        val mPriceFragment1 = SampleFragment.newInstance(2);
        val mPriceFragment2 = SampleFragment.newInstance(3);
        val mPriceFragment3 = SampleFragment.newInstance(5);
        val mPriceFragment4 = SampleFragment.newInstance(6);
        val mPriceFragment5 = SampleFragment.newInstance(7);
        val fragments = ArrayList<Fragment>()
        fragments.add(mPriceFragment)
        fragments.add(mPriceFragment1)
        fragments.add(mPriceFragment2)
        fragments.add(mPriceFragment3)
        fragments.add(mPriceFragment4)
        fragments.add(mPriceFragment5)
        viewPager.setAdapter(CommonFragmentAdapter(supportFragmentManager, fragments))
        indicator.setViewPager(viewPager, 0)
        title = tabNames[0]
        indicator.setIndicatorAdapter(object : PageIndicator.IndicatorAdapter {
            override fun getIndicatorView(position: Int): View? {
                val textView = mInflater.inflate(R.layout.layout_main_tab_item, null) as TextView
                textView.setText(tabNames[position])
                textView.setCompoundDrawablesWithIntrinsicBounds(0, tabDrawables[position], 0, 0)
                return textView
            }

            override fun onPageScrolled(view: View?, position: Int, selectPercent: Float) {
                //                Log.e("gfd",position.toString())
                //                title=tabNames[position]
            }
        })

        indicator.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                title = tabNames[position]
            }
        })
        val colorBar = ColorBar(mContext, mContext.resources.getColor(R.color.red_color))
        //        colorBar.setWidth(DimenUtils.dp2px(mContext, 80));

        indicator.setScrollBar(colorBar)

    }

    override fun setListener() {


        fab.setOnClickListener{
            mActivity.gotoPage3("robotone","1");
        }
    }

    override fun doClick(view: View?) {
    }

    override fun onDestroy() {
        EMClient.getInstance().logout(true);
    }
}
