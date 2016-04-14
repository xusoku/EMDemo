/*
 * BottomBar library for Android
 * Copyright (c) 2016 Iiro Krankka (http://github.com/roughike).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.xusoku.bledemo

import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import cn.kejin.gitbook.controllers.PageController
import cn.kejin.gitbook.controllers.PageDriver
import cn.nekocode.kotgo.sample.presentation.gotoMainPage
import com.example.xusoku.bledemo.adpter.base.FilmAdapter
import com.example.xusoku.bledemo.api.ApiService
import com.example.xusoku.bledemo.base.BaseFragment
import com.example.xusoku.bledemo.util.NetWorkUtils
import com.example.xusoku.bledemo.views.recyclerview.ExRcvAdapterWrapper
import com.example.xusoku.bledemo.views.recyclerview.LoadMoreRecyclerView
import kotlinx.android.synthetic.main.fragment_mian.*
import org.jetbrains.anko.async
import retrofit.Callback
import retrofit.GsonConverterFactory
import retrofit.Response
import retrofit.Retrofit
import java.util.*

/**
 */
class SampleFragment : BaseFragment() {
    override fun setListener() {

    }

    companion object {
        private val ARG_TEXT = "ARG_TEXT"

        fun newInstance(text: Int): SampleFragment {
            val args = Bundle()
            args.putInt(ARG_TEXT, text)
            val sampleFragment = SampleFragment()
            sampleFragment.arguments = args
            return sampleFragment
        }
    }

     var type : Int =1
    override fun initVariable() {
        type=arguments?.getInt(ARG_TEXT)?:0
        Log.e("str",""+type)
    }



    override fun setContentView(): Int {
        return  R.layout.fragment_mian
    }

    override fun findViews(view: View?) {
    }

    var list: MutableList<Grils.Gril> = arrayListOf()
    var filmadapter: FilmAdapter ?=null
    override fun initData() {

       val mStaggerLayoutManager=  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.setLayoutManager(mStaggerLayoutManager)
        filmadapter = FilmAdapter(activity)
        recyclerView.adapter= filmadapter

        pageDriver = PageDriver(null, recyclerView,pageCallback)
        startFragmentLoading()
    }

    val pageNum=10;
    var count : Int=1

    override fun onFragmentLoading() {
        super.onFragmentLoading()

        pageDriver.refresh()
    }

    lateinit var pageDriver : PageDriver

    val pageCallback =  object : PageDriver.ICallback {
        override fun onLoading(page: Int) {
            async() {
                Log.e("福利","==="+(page+1))
                var call = service.listGrils(type,page+1,pageNum)
                call.enqueue(object : Callback<Grils> {
                    override fun onFailure(t: Throwable?) {
                        //                            onFragmentLoadingFailed()
                        pageDriver.finish(PageController.Result.FAILED)
                    }

                    override fun onResponse(response: Response<Grils>?, retrofit: Retrofit?) {
                        onFragmentLoadingSuccess()
                        var gril = response?.body()
                        val num=gril?.tngou?.size?:0
                        if (gril!= null) {
                            filmadapter?.addAll(gril.tngou)
                        }

                        if (num == pageNum) {
                            pageDriver.finish(PageController.Result.SUCCESS)
                        }
                        else {
                            pageDriver.finish(PageController.Result.NO_MORE)
                        }
                    }
                })
            }
        }

        override fun onRefreshFailed() {
            onFragmentLoadingFailed()
        }

    }

}
