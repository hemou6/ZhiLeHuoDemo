package com.example.zhilehuodemo.View

import android.content.Context
import android.graphics.Canvas
import android.media.AudioManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import com.example.zhilehuodemo.R

class VerticalSeekBar @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet?=null,
    defaultStyle:Int= androidx.appcompat.R.attr.seekBarStyle
):AppCompatSeekBar(context,attrs,defaultStyle) {

    private var audioManager: AudioManager
    private val maxVolume:Int

    init {
        audioManager=context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val curVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        setProgress(curVolume)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight,measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(-90f)
        canvas.translate(-height.toFloat(),0f)
        super.onDraw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(!isEnabled)return false

        val x=height-event.y
        val progress=(x/height*max).toInt().coerceIn(0,max)

        when(event.action){
            MotionEvent.ACTION_DOWN->{
                parent.requestDisallowInterceptTouchEvent(true)
                setProgress(progress)
            }

            MotionEvent.ACTION_MOVE->{
                setProgress(progress)
                onSizeChanged(width,height,0,0)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0)
            }

            MotionEvent.ACTION_UP->{
                setProgress(progress)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0)
                onSizeChanged(width,height,0,0)
                parent.requestDisallowInterceptTouchEvent(false)
            }
            else->return super.onTouchEvent(event)
        }

        invalidate()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }
}