package com.example.zhilehuodemo

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.zhilehuodemo.Model.StoryContentDetail
import com.example.zhilehuodemo.Model.SentenceByXF
import com.example.zhilehuodemo.Uitls.DpToPx
import com.example.zhilehuodemo.Uitls.GlideApp
import com.example.zhilehuodemo.databinding.FragmentReadStoryBinding

class ReadStoryFragment:Fragment() {
    private val TAG="ReadStoryFragment"
    private lateinit var binding: FragmentReadStoryBinding
    private val ivStory:ImageView by lazy { binding.ivStory }
    private val ivLoudness:ImageView by lazy { binding.ivLoudness }
    private val ivState:ImageView by lazy { binding.ivState }
    private val ivReplay:ImageView by lazy { binding.ivReplay }
    private val storyContent:TextView by lazy { binding.storyContent }
    private val finger:ImageView by lazy { binding.finger }
    private val loudSeekBar by lazy { binding.loudSeekBar }

    private lateinit var storyContentDetail:StoryContentDetail
    private lateinit var sentenceList:List<SentenceByXF>
    private lateinit var audioUrl:String
    private lateinit var spanString:SpannableString

    private var endIndex=0            //黄色字体结束在数组中的下标
    private var startIndex=0          //黄色字体开始在数组中的下标
    private var curYellowIndex=0      //当前需要变换为黄色的字在整个句子中的下标
    private var curBlackIndex=0       //当前需要变换为黑色的字在整个句子中的下标
    private var runnable:Runnable?=null
    private var startTime:Long=0
    private var pauseTime:Long=0
    private var isPlaying:Boolean=false
    private var isEnd:Boolean=false

    private var pageNum:Int=0
    private val mediaPlayer by lazy { MediaPlayer() }
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val animator:ObjectAnimator by lazy { getFingerAnimation() }

    companion object{
        fun getInstance(storyContentDetail: StoryContentDetail)=ReadStoryFragment().apply {
            arguments=Bundle().apply {
                putParcelable("storyContentDetail",storyContentDetail)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {bundle ->
            storyContentDetail=bundle.getParcelable("storyContentDetail")?:
                throw IllegalArgumentException("Missing storyContentDetail argument")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentReadStoryBinding.inflate(layoutInflater)
        init()

        ivState.setOnClickListener{
            changeState()
        }

        ivReplay.setOnClickListener{
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
            ivState.setImageResource(R.drawable.ic_pause)

            changeTextColor(0,storyContent.text.length,R.color.black)
            pauseTime=0       //触发更新startTime
            curYellowIndex=0
            curBlackIndex=0
            endIndex=0
            startIndex=0
            isPlaying=true
            isEnd=false
            runnable?.let { handler.removeCallbacks(it) }
            startTimeCount()

            if(animator.isRunning){
                animator.cancel()
                finger.visibility=View.GONE
            }
        }

        ivLoudness.setOnClickListener{
            if(loudSeekBar.visibility==View.GONE){
                loudSeekBar.visibility=View.VISIBLE
            }else{
                loudSeekBar.visibility=View.GONE
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(!isPlaying)mediaPlayer.seekTo(0)
        mediaPlayer.start()
        ivState.setImageResource(R.drawable.ic_pause)
        startTimeCount()
        isPlaying=true
        isEnd=false
    }

    override fun onPause() {
        super.onPause()
        if(isPlaying)mediaPlayer.pause()
        pauseTime=0
        curYellowIndex=0
        curBlackIndex=0
        endIndex=0
        startIndex=0
        runnable?.let { handler.removeCallbacks(it) }
        isPlaying=false
        changeTextColor(0,storyContent.text.length,R.color.black)
        finger.visibility=View.GONE
        if(animator.isRunning){
            animator.cancel()
            finger.visibility=View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        isPlaying=false
    }

    private fun init(){
        GlideApp.with(requireActivity())
            .load(storyContentDetail.imgUrl)
            .error(R.drawable.ic_error_vector)
            .listener(object :RequestListener<Drawable>{
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("GlideError", "加载失败: ${e?.message}")
                    e?.rootCauses?.forEachIndexed { index, throwable ->
                        Log.e("GlideError", "根本原因 $index: ${throwable.message}")
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("Glide", "加载成功: ${model.toString()}")
                    return false
                }

            })
            .transform(RoundedCorners(60.DpToPx(requireActivity())))
            .into(ivStory)

        storyContent.text=storyContentDetail.sentence
        spanString=SpannableString(storyContent.text)

        audioUrl=storyContentDetail.audioUrl
        pageNum=storyContentDetail.pageNum

        sentenceList=storyContentDetail.sentenceByXFList

        initMediaPlayer()
    }

    private fun initMediaPlayer(){
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepare()
    }

    private fun changeState(){
        if(isEnd==true)return

        if(isPlaying){
            ivState.setImageResource(R.drawable.ic_play)
            mediaPlayer.pause()
            pauseTimeCount()
            isPlaying=false
        }else{
            ivState.setImageResource(R.drawable.ic_pause)
            mediaPlayer.start()
            startTimeCount()
            isPlaying=true
        }
    }

    private fun pauseTimeCount(){
        pauseTime=System.currentTimeMillis()
        runnable?.let { handler.removeCallbacks(it) }
    }

    private fun startTimeCount(){       //开始计时
        if(pauseTime==0L){
            startTime=System.currentTimeMillis()
        }else{
            val hadPlayTime=pauseTime-startTime      //之前已经播放过了的音频时间
            startTime=System.currentTimeMillis()-hadPlayTime
        }

        runnable = object :Runnable{
            override fun run() {
                val curTime=System.currentTimeMillis()-startTime
                checkColorUpdate(curTime)
                handler.postDelayed(this,10)
            }
        }

        handler.post (runnable!!)
    }

    private fun checkColorUpdate(curTime:Long){
        if(startIndex<sentenceList.size && curTime>sentenceList[startIndex].wb){
            changeTextColor(curYellowIndex,curYellowIndex+sentenceList[startIndex].word.length,R.color.color_FFAE32)
            curYellowIndex+=sentenceList[startIndex].word.length
            startIndex++
        }

        if(endIndex<sentenceList.size && curTime>sentenceList[endIndex].we){
            changeTextColor(curBlackIndex,curBlackIndex+sentenceList[endIndex].word.length,R.color.black)
            curBlackIndex+=sentenceList[endIndex].word.length
            endIndex++
            if(endIndex==sentenceList.size){
                isEnd=true
                showEndAnimation()
            }
            runnable?.let { handler.removeCallbacks(it) }      //播放结束移除runnable任务
        }
    }

    private fun changeTextColor(startIndex:Int,endIndex:Int,color:Int){
        if(endIndex>storyContent.text.length)return
        spanString.setSpan(
            ForegroundColorSpan(requireActivity().resources.getColor(color)),
            startIndex,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        storyContent.text=spanString
    }

    private fun showEndAnimation(){
        finger.visibility=View.VISIBLE
        animator.start()
    }

    private fun getFingerAnimation():ObjectAnimator{
        return ObjectAnimator.ofFloat(finger,"translationX",-250f).apply {
            duration=2000
            repeatCount=ValueAnimator.INFINITE
            repeatMode=ValueAnimator.RESTART
        }
    }
}