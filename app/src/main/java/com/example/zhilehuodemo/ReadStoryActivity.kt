package com.example.zhilehuodemo

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.zhilehuodemo.Adapter.ReadStoryAdapter
import com.example.zhilehuodemo.Model.StoryDetail
import com.example.zhilehuodemo.Net.RetrofitUtil
import com.example.zhilehuodemo.databinding.ActivityReadStoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReadStoryActivity : AppCompatActivity() {
    private lateinit var binding:ActivityReadStoryBinding
    private val toolbar:LinearLayout by lazy { binding.toolbar }
    private val progressBar:ProgressBar by lazy { binding.progressBar }
    private val viewPager:ViewPager2 by lazy { binding.viewPager }
    private val tv_curPage:TextView by lazy { binding.curPage }
    private val tv_totalPage:TextView by lazy { binding.totalPage }
    private val back:ImageView by lazy { binding.back }

    private lateinit var storyDetail: StoryDetail
    private var pageNum=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityReadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop,v.paddingRight, systemBars.bottom)
            toolbar.setPadding(toolbar.paddingLeft,systemBars.top,toolbar.paddingRight,toolbar.paddingBottom)
            WindowInsetsCompat.CONSUMED
        }

        initData()

        viewPager.offscreenPageLimit=2      //尝试缓存前后两个界面

        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                progressBar.progress= (((position+1.0)/pageNum)*100.0).toInt()
                tv_curPage.text=(position+1).toString()
            }
        })

        back.setOnClickListener{
            finish()
        }
    }

    private fun initData(){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val response=RetrofitUtil.readStoryApi.getStoryDetail(6)
                when(response.code){
                    0->{
                        storyDetail=response.data!!
                        withContext(Dispatchers.Main){
                            val adapter=ReadStoryAdapter(this@ReadStoryActivity)
                            adapter.submitList(storyDetail.contentList)
                            viewPager.adapter=adapter
                            pageNum=storyDetail.contentList.size
                            tv_totalPage.text=pageNum.toString()
                        }
                    }
                    else->{
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ReadStoryActivity,"网络错误", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}