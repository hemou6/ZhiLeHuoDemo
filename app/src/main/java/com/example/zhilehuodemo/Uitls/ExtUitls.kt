package com.example.zhilehuodemo.Uitls

import android.content.Context
import android.util.TypedValue

fun Int.forceToTwo():String{
    if(this<10){
        return "0$this"
    }else{
        return this.toString()
    }
}

fun Int.DpToPx(context: Context):Int=TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    context.resources.displayMetrics
).toInt()