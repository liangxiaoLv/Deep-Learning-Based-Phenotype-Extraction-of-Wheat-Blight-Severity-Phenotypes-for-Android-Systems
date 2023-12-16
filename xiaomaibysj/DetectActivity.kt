package com.example.xiaomaibysj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView

class DetectActivity : AppCompatActivity() {
    private lateinit var tupian: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_detect)

        val buttonJiance:Button = findViewById(R.id.button_jinrujiance)
        tupian = findViewById(R.id.image_view_detect)
        val buttonReadData:Button = findViewById(R.id.button_data)
        val buttonExit:Button = findViewById(R.id.button_exit)

        buttonExit.setOnClickListener {
            finish()
        }

        buttonReadData.setOnClickListener {
            val intent = Intent(this,ListActivity::class.java)
            startActivity(intent)
        }

        buttonJiance.setOnClickListener {
            val jiance:Intent = Intent(this,jianCe::class.java)
            startActivity(jiance)
        }



    }

}