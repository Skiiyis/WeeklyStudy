package com.skiiyis.study.hotfix

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.text).text = problemMethod()
        checkPatch()
    }

    private fun checkPatch() {
        HotFixCheckUtil.checkAndHotFix(this)
    }

    private fun problemMethod(): String {
        return "I'm problem method!!"
    }

}