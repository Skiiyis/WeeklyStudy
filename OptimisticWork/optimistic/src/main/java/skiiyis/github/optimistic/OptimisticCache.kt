package skiiyis.github.optimistic

import okhttp3.Request

interface OptimisticCache {
    fun putCache(request: Request, result: Any?)
    fun getCache(request: Request): Any?
}

class SimpleCache : OptimisticCache {

    companion object {
        val cache = mutableMapOf<Request, Any?>()
    }

    override fun putCache(request: Request, result: Any?) {
        cache[request] = result
    }

    override fun getCache(request: Request): Any? {
        return cache[request]
    }
}