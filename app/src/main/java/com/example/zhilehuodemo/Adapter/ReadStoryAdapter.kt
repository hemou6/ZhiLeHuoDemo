package com.example.zhilehuodemo.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.zhilehuodemo.Model.StoryContentDetail
import com.example.zhilehuodemo.ReadStoryFragment

class ReadStoryAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    private val storyContentList= mutableListOf<StoryContentDetail>()

    fun submitList(list:List<StoryContentDetail>){
        storyContentList.clear()
        storyContentList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =storyContentList.size

    override fun createFragment(position: Int): Fragment =ReadStoryFragment.getInstance(storyContentList[position])


}