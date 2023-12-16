package com.example.xiaomaibysj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class AboutWheatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN,)
        setContentView(R.layout.activity_about_wheat)
        title = "关于小麦赤霉病"

        val imageView = findViewById<ImageView>(R.id.imageView_aw)
        val textView = findViewById<TextView>(R.id.textView_aw)
        imageView.setImageResource(R.drawable.wheat_ill)
        textView.movementMethod = ScrollingMovementMethod()
        textView.text = getString(R.string.my_text)
    }
}