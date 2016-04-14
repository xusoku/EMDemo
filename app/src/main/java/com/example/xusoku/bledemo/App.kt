package com.example.xusoku.bledemo

import android.app.Application
import com.example.xusoku.bledemo.EcUtil.DemoHelper
import com.example.xusoku.bledemo.util.CommonManager
import kotlin.properties.Delegates

/**
 * Created by xusoku on 2016/4/7.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        CommonManager.getInstance().initializeCommon(this)
        DemoHelper.getInstance().init(instance)
    }

    companion object {
        var instance by Delegates.notNull<App>()
    }

}
