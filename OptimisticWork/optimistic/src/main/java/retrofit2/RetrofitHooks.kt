package retrofit2

import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import skiiyis.github.optimistic.Optimistic
import skiiyis.github.optimistic.OptimisticCall
import skiiyis.github.optimistic.OptimisticRequestInfo
import java.lang.reflect.Method


/**
 * 1. 收集哪些接口需要做optimistic
 *  - RetrofitService打了注解的方法
 *  -
 */
object RetrofitHooks {

    internal val cache = mutableSetOf<HttpServiceMethod<*, *>>()

    /**
     *  插桩到 HttpServiceMethod.parseAnnotations
     *  收集真实的业务url, request, header, body等数据
     *  保留一个request信息和HttpServiceMethod的映射
     *  真正构造完整的request的方法在RequestFactory的create方法
     */
    internal fun parseOptimisticRequest(
        httpServiceMethod: HttpServiceMethod<*, *>,
        method: Method
    ) {
        val optimisticAnnotation = method.getAnnotation(Optimistic::class.java) ?: return
        // 解析一些注解上的参数
        cache.add(httpServiceMethod)
    }

    /**
     * 插桩在 HttpServiceMethod.invoke
     */
    /*internal fun handleOptimisticRequest(
        httpServiceMethod: HttpServiceMethod<*, *>,
        call: Call<Any>
    ): Call<Any> {
        if (!cache.contains(httpServiceMethod)) return call
        // 根据注解缓存请求对应的响应
        return OptimisticCall(call)
    }*/

    /**
     * 插桩在 Retrofit.nextCallAdapter, 检查到如果返回的是 RxJava2CallAdapter 则返回我们自己的Adapter
     * 我们的 Adapter 会对 RxJava2CallAdapter 进行一个包装
     */
    internal fun <R, T> handleOptimisticRequest(
        annotations: Array<Annotation>,
        callAdapter: CallAdapter<R, T>
    ): CallAdapter<R, T> {
        val hasOptimistic = annotations.find { it is Optimistic } ?: return callAdapter
        // 读取注解信息
        RxJava2CallAdapterFactory.create()
        throw IllegalArgumentException("")
    }

}