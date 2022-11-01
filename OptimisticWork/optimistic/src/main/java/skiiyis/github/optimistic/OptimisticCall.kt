package skiiyis.github.optimistic

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OptimisticCall<T>(
    private val rawCall: Call<T>
) : Call<T> {

    private var cacheResponse: Response<T>? = null

    override fun clone(): Call<T> {
        return OptimisticCall(rawCall)
    }

    fun cacheResponse(response: Response<T>) {

    }

    override fun execute(): Response<T> {
        val response = cacheResponse
        if (response == null) {
            val newResponse = rawCall.execute()
            cacheResponse(newResponse)
            return newResponse
        } else {
            return response
        }
    }

    override fun enqueue(callback: Callback<T>) {
        val response = cacheResponse
        if (response == null) {
            rawCall.enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    cacheResponse(response)
                    callback.onResponse(call, response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onFailure(call, t)
                }
            })
        } else {
            callback.onResponse(this, response)
        }
    }

    override fun isExecuted(): Boolean {
        val response = cacheResponse
        if (response == null) {
            return rawCall.isExecuted
        } else {
            return false
        }
    }

    override fun cancel() {
        val response = cacheResponse
        if (response == null) {
            rawCall.cancel()
        }
    }

    override fun isCanceled(): Boolean {
        val response = cacheResponse
        if (response == null) {
            return rawCall.isCanceled
        } else {
            return false
        }
    }

    override fun request(): Request {
        return rawCall.request()
    }

    override fun timeout(): Timeout {
        val response = cacheResponse
        if (response == null) {
            return rawCall.timeout()
        } else {
            return Timeout.NONE
        }
    }
}