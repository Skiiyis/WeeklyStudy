package com.skiiyis.studykapt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.skiiyis.libprocessor.AutoInject
import com.skiiyis.libprocessor.Optional
import com.skiiyis.libprocessor.Required

@AutoInject
class MainActivity : AppCompatActivity() {

    @Required
    lateinit var id: String

    @Required
    lateinit var name: String

    @Optional
    @JvmField
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityParser.inject(this)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.content).text = "title:$title\nname:$name\nid:$id"
    }
}