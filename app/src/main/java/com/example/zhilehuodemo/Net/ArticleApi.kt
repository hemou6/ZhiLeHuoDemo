package com.example.zhilehuodemo.Net

import com.example.zhilehuodemo.Model.ArticlePage
import com.example.zhilehuodemo.Model.MyResponse
import com.example.zhilehuodemo.Model.Type
import okhttp3.Cookie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleApi {
    @GET("englishgpt/library/articleTypeList")
    suspend fun getTypeList():MyResponse<List<Type>>

    @GET("englishgpt/appArticle/selectList")
    suspend fun getDifficultyList():MyResponse<List<Int>>

    @GET("englishgpt/library/articleList")
    suspend fun getArticleList(
        @Query("lexile") lexile:Int,
        @Query("typeId") typeId:Int,
        @Query("page") page:Int,
        @Query("size") size:Int,
        @Header("Cookie") cookie: String="sid=OFvEbpyl4PyKkc/cSjl2tW3g5Ga/z5DPSQRGQn8mJBs="
    ):MyResponse<ArticlePage>
}