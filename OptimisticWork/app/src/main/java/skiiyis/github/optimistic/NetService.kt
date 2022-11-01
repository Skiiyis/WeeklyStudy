package skiiyis.github.optimistic

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface NetService {

    @Optimistic(expiredTime = 1000L)
    @GET("/fetch/somthing/{id}")
    fun fetchSomething(
        @Query("fetch") fetch: String,
        @Header("x-token") token: String
    ): Observable<ResponseBody>

    @GET("/fetch/{id}/")
    fun fetchSomething2(
        @Path("id") id: String
    ): Observable<ResponseBody>

    @GET("friend/json/")
    fun fetch(): Observable<DataResponse>
}