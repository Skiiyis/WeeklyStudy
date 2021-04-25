package com.skiiyis.studykapt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skiiyis.libprocessor.Args

class MainActivity : AppCompatActivity() {

    @Args(required = true)
    lateinit var id: String

    @Args(required = true)
    lateinit var name: String

    @Args(required = false)
    @JvmField
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // MainActivityParser.parse(this)
        setContentView(R.layout.activity_main)
    }
}