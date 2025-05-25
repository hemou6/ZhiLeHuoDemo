package com.example.zhilehuodemo.Net

import com.example.zhilehuodemo.Model.MyResponse
import com.example.zhilehuodemo.Model.StoryDetail
import okhttp3.Cookie
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReadStoryApi {
    @GET("knowledge/article/getArticleDetail")
    suspend fun getStoryDetail(
        @Query("aid") aid:Int,
        @Header("Cookie") cookie:String="sid=i5VMMK2c7EEm5qK597kJeDqrel7NKCRqSQRGQn8mJBs="
    ):MyResponse<StoryDetail>
}