package com.skiiyis.studykapt

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        findViewById<View>(R.id.clickMe).setOnClickListener {
            MainActivityLauncher.builder("this is id", "I'm name")
                .requestCode(10)
                .title("This is title")
                .start(this)
        }
    }
}