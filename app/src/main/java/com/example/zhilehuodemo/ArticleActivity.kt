package com.example.zhilehuodemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView

import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zhilehuodemo.Adapter.ArticleAdapter
import com.example.zhilehuodemo.Adapter.ArticleTypeAdapter
import com.example.zhilehuodemo.Event.RefreshEvent
import com.example.zhilehuodemo.Model.Article
import com.example.zhilehuodemo.Model.Type
import com.example.zhilehuodemo.Net.RetrofitUtil
import com.example.zhilehuodemo.databinding.ActivityArticleBinding
import com.google.android.material.tabs.TabLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ArticleActivity : AppCompatActivity() {
    private val TAG="ArticleActivity"
    private lateinit var binding:ActivityArticleBinding
    private val typeRecyclerView:RecyclerView by lazy { binding.typeList }
    private val difficultyTabLayout: TabLayout by lazy { binding.difficulty }
    private val contentRecyclerView:RecyclerView by lazy { binding.articleList }
    private val tvNoContent:TextView by lazy { binding.tvNoContent }
    private val smartRefreshLayout:SmartRefreshLayout by lazy { binding.smartRefreshLayout }

    private val typeList= mutableListOf<Type>()
    private lateinit var typeAdapter: ArticleTypeAdapter
    private val difficultyList= mutableListOf<Int>()
    private var articleList= mutableListOf<Article>()
    private val articleAdapter by lazy { ArticleAdapter() }

    private val handle=Handler(Looper.getMainLooper())

    private var currentDiffTabPosition=0

    private var page=1
    private val size=10
    private var hasNext=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
            insets
        }

        EventBus.getDefault().register(this)
        initData()

        difficultyTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(currentDiffTabPosition!=tab.position){
                    refresh(0,size)
                    currentDiffTabPosition=tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })

        contentRecyclerView.layoutManager=LinearLayoutManager(this@ArticleActivity)
        contentRecyclerView.adapter=articleAdapter

        smartRefreshLayout.autoRefresh()

        smartRefreshLayout.setOnRefreshListener{
            refresh(0,size)
            if(it.isRefreshing)it.finishRefresh(5000)
        }
        smartRefreshLayout.setOnLoadMoreListener {
            if(hasNext){
                LoadMore()
                if (it.isLoading)it.finishLoadMore(5000)
            }else{
                it.finishLoadMoreWithNoMoreData()
            }
        }
    }

    private fun initData(){
        lifecycleScope.launch {
            initTypeList()
            typeAdapter= ArticleTypeAdapter(typeList)
            typeRecyclerView.layoutManager=LinearLayoutManager(this@ArticleActivity)
            typeRecyclerView.adapter=typeAdapter

            initDifficultyList()
            difficultyList.forEach { diff->
                val tab=difficultyTabLayout.newTab().setText(diff.toString())
                difficultyTabLayout.addTab(tab)
            }

        }
    }

    suspend private fun initTypeList(){
        withContext(Dispatchers.IO){
            val response=RetrofitUtil.articleApi.getTypeList()
            when(response.code){
                0->{
                    typeList.addAll(response.data!!)
                }
                else->{
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@ArticleActivity,"网络错误",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    suspend private fun initDifficultyList(){
        withContext(Dispatchers.IO){
            val response=RetrofitUtil.articleApi.getDifficultyList()
            when(response.code){
                0->{
                    difficultyList.addAll(response.data!!)
                }
                else->{
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@ArticleActivity,"网络错误",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun showContent(){
        tvNoContent.visibility=View.GONE
        contentRecyclerView.visibility=View.VISIBLE
    }

    private fun showNoContent(){
        tvNoContent.visibility=View.VISIBLE
        contentRecyclerView.visibility=View.GONE
    }

    @Subscribe
    fun onRefresh(event:RefreshEvent){
        if(event.name=="onRefresh"){
            refresh(0,size)
        }
    }

    private fun refresh(page:Int,size:Int){
        lifecycleScope.launch {
            withContext(Dispatchers.IO){
                val lexile=difficultyList[difficultyTabLayout.selectedTabPosition]
                val type=typeAdapter.getCurrentTypeId()
                val response=RetrofitUtil.articleApi.getArticleList(lexile,type,page,size)
                when(response.code){
                    0->{
                        val articlePage=response.data!!
                        hasNext=articlePage.hasNextPage
                        val newList=ArrayList(articleList)
                        if (page==0)newList.clear()

                        if(articlePage.firstPage==0) {
                            withContext(Dispatchers.Main){
                                showNoContent()
                            }
                        }
                        else{
                            newList.addAll(articlePage.list)

                        }
                        articleList=newList
                    }
                    else->{
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@ArticleActivity,"网络错误",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            withContext(Dispatchers.Main){
                if(articleList.isNotEmpty()){
                    articleAdapter.submitList(articleList)
                    showContent()
                }
                if(smartRefreshLayout.isRefreshing)smartRefreshLayout.finishRefresh()
                if(smartRefreshLayout.isLoading)smartRefreshLayout.finishLoadMore()
            }
        }
    }

    private fun LoadMore(){
        page++
        refresh(page,size)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}