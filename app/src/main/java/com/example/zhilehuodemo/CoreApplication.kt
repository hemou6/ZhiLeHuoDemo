package com.example.zhilehuodemo

import android.app.Application
import android.content.Context
import android.os.Debug
import com.tencent.mmkv.MMKV

class CoreApplication:Application() {
    companion object{
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext=applicationContext
        initMMKV()
    }

    private fun initMMKV(){
        MMKV.initialize(this@CoreApplication)
    }
}