package retrofit2.adapter.rxjava2

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import skiiyis.github.optimistic.Optimistic
import skiiyis.github.optimistic.SimpleCache
import java.lang.reflect.Type

class OptimisticCallAdapter<R>(
    private val rxCallAdapter: CallAdapter<R, Any>,
    private val annotation: Optimistic
) : CallAdapter<R, Any> {

    private val simpleCache = SimpleCache()

    override fun responseType(): Type {
        return rxCallAdapter.responseType()
    }

    override fun adapt(call: Call<R>): Any {
        val originRet = rxCallAdapter.adapt(call)
        val originObservable = (originRet as? Observable<Response<R>>) ?: return originRet
        val o = Observable.create<Response<R>> { emitter ->
            val expiredTime = annotation.expiredTime
            val cacheR = simpleCache.getCache(call.request()) as R
            if (cacheR != null) {
                emitter.onNext(Response.success(cacheR))
            }
            val ignore = originObservable
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.isSuccessful) {
                        val newBody = it.body()
                        // equals 可换成compare
                        if (newBody?.equals(cacheR) == true) {
                            // 更新缓存并发送新数据
                            simpleCache.putCache(call.request(), it.body())
                            emitter.onNext(Response.success(newBody))
                        } else {
                            // 刷新缓存时间
                        }
                    }
                }, {
                    emitter.onError(it)
                })
        }
        return o
    }
}