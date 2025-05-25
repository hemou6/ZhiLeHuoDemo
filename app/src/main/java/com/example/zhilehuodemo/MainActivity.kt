package com.example.zhilehuodemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.zhilehuodemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.firstBtn.setOnClickListener {
            val intent=Intent(this,InformationActivity::class.java)
            startActivity(intent)
        }

        binding.secondBtn.setOnClickListener {
            val intent=Intent(this,ArticleActivity::class.java)
            startActivity(intent)
        }

        binding.thirdBtn.setOnClickListener {
            val intent=Intent(this,ReadStoryActivity::class.java)
            startActivity(intent)
        }
    }
}