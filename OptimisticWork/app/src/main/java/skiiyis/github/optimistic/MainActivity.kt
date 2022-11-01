package skiiyis.github.optimistic

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(TextView(this).also {
            it.text = "is Me!"
            it.setOnClickListener {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.wanandroid.com/")
                    .client(OkHttpClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .build()
                retrofit.create(NetService::class.java)
                    .fetch()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        Log.e("tag", it.errorMsg.toString())
                    }, {
                        Log.e("tag", it.toString())
                    })
            }
        })
    }
}