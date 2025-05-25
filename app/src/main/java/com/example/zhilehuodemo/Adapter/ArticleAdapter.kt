package com.example.zhilehuodemo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zhilehuodemo.Adapter.DiffCallBack.ArticleDiffCallBack
import com.example.zhilehuodemo.Model.Article
import com.example.zhilehuodemo.R

class ArticleAdapter(): ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallBack()) {
    inner class ArticleViewHolder(view:View):RecyclerView.ViewHolder(view){
        val title:TextView=view.findViewById(R.id.title)
        val type:TextView=view.findViewById(R.id.type)
        val diff:TextView=view.findViewById(R.id.diff)
        val wordNum:TextView=view.findViewById(R.id.word_num)
        val cover:ImageView=view.findViewById(R.id.cover)
        val stage:TextView=view.findViewById(R.id.stage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_article_content, parent, false)
        return ArticleViewHolder(view)
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article=getItem(position)
        holder.title.text=article.title
        holder.type.text=article.type
        holder.diff.text=article.lexile.toString()
        holder.wordNum.text=article.wordNum.toString()
        if(article.stage.isNotEmpty()){
            holder.stage.text=article.stage
        }else{
            holder.stage.visibility=View.GONE
        }
        Glide.with(holder.itemView.context)
            .load(article.cover)
            .into(holder.cover)
    }



}