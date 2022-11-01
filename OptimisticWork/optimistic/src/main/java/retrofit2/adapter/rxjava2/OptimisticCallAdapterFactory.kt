package retrofit2.adapter.rxjava2

import retrofit2.CallAdapter
import retrofit2.Retrofit
import skiiyis.github.optimistic.Optimistic
import java.lang.reflect.Type

class OptimisticCallAdapterFactory : CallAdapter.Factory() {

    private lateinit var rxJava2CallAdapterFactory: RxJava2CallAdapterFactory

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        val rxCallAdapter = rxJava2CallAdapterFactory.get(returnType, annotations, retrofit)
        val optimisticAnnotation = annotations.find { it is Optimistic } as? Optimistic
            ?: return rxCallAdapter
        // 解析注解参数
        return OptimisticCallAdapter(rxCallAdapter as CallAdapter<Any, Any>, optimisticAnnotation)
    }
}