package com.skiiyis.studykapt

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // @Args(required = true)
    lateinit var id: String

    // @Args(required = true, default = "name")
    lateinit var name: String

    // @Args(required = false, default = "title")
    var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityParser.parse(this)
        setContentView(R.layout.activity_main)
    }
}

class MainActivityParser {
    companion object {
        fun parse(activity: MainActivity) {
            val intent = activity.intent
            activity.id = intent.getStringExtra("id") ?: throw Exception("not default")
            activity.name = intent.getStringExtra("name") ?: "name"
            activity.title = intent.getStringExtra("title") ?: "title"
        }
    }
}

class MainActivityLauncher private constructor() {

    companion object {
        fun create(activity: Activity): MainActivityLauncher {
            return MainActivityLauncher().requireContext(activity)
        }
    }

    private lateinit var activity: Activity
    fun requireContext(activity: Activity): MainActivityLauncher {
        this.activity = activity
        return this
    }

    fun requireParams(id: String, name: String = "name"): OptionalLauncher {
        return OptionalLauncher(activity, id, name)
    }

    class OptionalLauncher(
        private val activity: Activity,
        private val id: String,
        private val name: String
    ) {

        private var title: String? = "title"
        private var subTitle: String? = null

        fun optionalParams(title: String? = "title", subTitle: String? = null): OptionalLauncher {
            this.title = title
            this.subTitle = subTitle
            return this
        }

        fun launch() {
            activity.startActivity(Intent().also {
                it.component = ComponentName(
                    "com.skiiyis.studykapt",
                    "com.skiiyis.studykapt.MainActivity"
                )
                it.putExtra("title", title)
                it.putExtra("subTitle", subTitle)
                it.putExtra("id", id)
                it.putExtra("name", name)
            })
        }
    }
}