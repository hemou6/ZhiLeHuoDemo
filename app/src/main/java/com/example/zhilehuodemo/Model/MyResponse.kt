package com.example.zhilehuodemo.Model

data class MyResponse<T>(
    val code:Int,
    val msg:String,
    val data:T?
)