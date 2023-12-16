package com.example.xiaomaibysj

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        val subjectEditText = findViewById<EditText>(R.id.subject_edit_text)
        val contentEditText = findViewById<EditText>(R.id.content_edit_text)
        val sendButton = findViewById<Button>(R.id.send_button)
        val textView:TextView = findViewById(R.id.lxfs)

        sendButton.setOnClickListener {
            val subject = subjectEditText.text.toString()
            val content = contentEditText.text.toString()

            if (subject.isNotEmpty() && content.isNotEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto:")

                // 设置收件人邮箱地址
                val recipientEmail = "1214445268@qq.com"
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(Intent.EXTRA_TEXT, content)

                if (emailIntent.resolveActivity(packageManager) != null) {
                    startActivity(emailIntent)
                } else {
                    Toast.makeText(this, "您的邮箱不可用，您可以通过上方提供的联系方式与我们联系", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "请填写邮件主题和内容", Toast.LENGTH_SHORT).show()
            }
        }
    }
}