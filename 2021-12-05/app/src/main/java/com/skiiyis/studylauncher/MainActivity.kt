package com.skiiyis.studylauncher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skiiyis.study.launcher.Launcher

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Launcher.beginTransaction(ColdLaunchTransaction.NAME)?.commit()
    }
}