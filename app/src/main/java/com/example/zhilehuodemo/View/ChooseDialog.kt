package com.example.zhilehuodemo.View

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.TextView
import com.example.zhilehuodemo.R

class ChooseDialog(context: Context,
    title:String,
    content:String,
    onSure:()->Unit
):Dialog(context) {
    init {
        val view=LayoutInflater.from(context).inflate(R.layout.dialog_choose,null)
        setContentView(view)


        findViewById<TextView>(R.id.title).text=title

        findViewById<TextView>(R.id.content).text=content

        findViewById<TextView>(R.id.cancel).setOnClickListener {
            dismiss()
        }

        findViewById<TextView>(R.id.sure).setOnClickListener {
            onSure()
            dismiss()
        }

        setCanceledOnTouchOutside(false)            //设置不能点击旁边取消dialog

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setWindowAnimations(R.style.ChooseDialogAnimation)
    }
}