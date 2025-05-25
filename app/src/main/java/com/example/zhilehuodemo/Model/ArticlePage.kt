package com.example.zhilehuodemo.Model

data class ArticlePage(
    val total:Int=0,
    val list: List<Article>,
    val hasNextPage:Boolean=false,
    val hasPreviousPage:Boolean=false,
    val firstPage:Int=0,
    val lastPage:Int=0,
)
