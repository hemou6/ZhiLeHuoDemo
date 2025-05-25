package com.example.zhilehuodemo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zhilehuodemo.Event.RefreshEvent
import com.example.zhilehuodemo.Model.Type
import com.example.zhilehuodemo.R
import org.greenrobot.eventbus.EventBus

class ArticleTypeAdapter(
   val typeList: List<Type>
):RecyclerView.Adapter<ArticleTypeAdapter.ArticleTypeViewHolder>() {

    private var currentPosition=0


    inner class ArticleTypeViewHolder(view:View):RecyclerView.ViewHolder(view){
        val type=view.findViewById<TextView>(R.id.type)
        val flag=view.findViewById<ImageView>(R.id.iv_flag)
        val isSelect=view.findViewById<View>(R.id.is_select)

        var id=-1

        init {
            itemView.setOnClickListener {
                if(adapterPosition!=currentPosition){
                    val lastPosition=currentPosition
                    currentPosition=adapterPosition
                    itemView.setBackgroundResource(R.color.white)
                    type.setTextColor(ContextCompat.getColor(itemView.context,R.color.color_28A3FF))
                    isSelect.visibility=View.VISIBLE
                    notifyItemChanged(lastPosition)

                    EventBus.getDefault().post(RefreshEvent("onRefresh"))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleTypeViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_article_type,parent,false)
        return ArticleTypeViewHolder(view)
    }

    override fun getItemCount(): Int =typeList.size+1

    override fun onBindViewHolder(holder: ArticleTypeViewHolder, position: Int) {
        if(holder.id==-1){
            if(position==0){
                holder.flag.setImageResource(R.drawable.ic_flag)
                holder.type.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.color_28A3FF))
                holder.id=0
            }else{
                holder.id=typeList[position-1].id
                holder.type.text=typeList[position-1].type
            }
        }

        if(currentPosition!=position){
            holder.type.setTextColor(ContextCompat.getColor(holder.itemView.context,R.color.black))
            holder.itemView.setBackgroundResource(R.color.color_F3F3F3)
            holder.isSelect.visibility=View.INVISIBLE
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position             //禁用缓存
    }


    fun getCurrentTypeId():Int{
        if(currentPosition==0)return 0
        else return typeList[currentPosition-1].id
    }

}