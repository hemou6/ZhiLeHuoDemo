package com.example.zhilehuodemo.Adapter.DiffCallBack

import androidx.recyclerview.widget.DiffUtil
import com.example.zhilehuodemo.Model.Article
import kotlin.math.sign

class ArticleDiffCallBack(
):DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem==newItem
    }


}