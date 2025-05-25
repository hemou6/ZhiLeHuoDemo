package com.example.zhilehuodemo.Net

import com.example.zhilehuodemo.Model.MyResponse
import com.example.zhilehuodemo.Model.Type
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Query


class Repository private constructor(){
    companion object{
        val instance:Repository by lazy { Repository() }
    }

//    private val service by lazy { RetrofitUtil.instanceArticle.create(ArticleApi::class.java) }
//
//    fun getTypeList()= flow {
//        emit(service.getTypeList())
//    }
//
//    fun getDifficultyList()= flow {
//        emit(service.getDifficultyList())
//    }
//
//    fun getArticleList(
//        lexile:Int,
//        typeId:Int,
//        page:Int,
//        size:Int,)= flow { emit(service.getArticleList(lexile,typeId,page,size))  }
}