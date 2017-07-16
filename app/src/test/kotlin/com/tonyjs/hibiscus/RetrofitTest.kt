package com.tonyjs.hibiscus

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.util.concurrent.TimeUnit
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type


class RetrofitTest {

    @Test
    fun test() {
        println(javaClass.getResource("/abc.jpg").path)

        val file = File(javaClass.getResource("/abc.jpg").path)
        println(file.path)

        val retrofit = Retrofit.Builder()
                .baseUrl("http://telegra.ph")
                .client(OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(ToStringConverterFactory())
                .build()


        retrofit.create(Api::class.java).run {
            val body = MultipartBody.create(MediaType.parse("image/jpeg"), file)
            upload(MultipartBody.Part.createFormData("file", "abc.jpg", body)).run {
                println(execute().body())
            }
        }

    }

}

interface Api {

    @Multipart
    @POST("http://telegra.ph/upload")
    fun upload(@Part file: MultipartBody.Part): Call<String>

}

class ToStringConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
        if (String::class.java == type) {
            return Converter<ResponseBody, String> { value -> value.string() }
        }
        return null
    }

    override fun requestBodyConverter(type: Type, parameterAnnotations: Array<Annotation>,
                                      methodAnnotations: Array<Annotation>, retrofit: Retrofit): Converter<*, RequestBody>? {

        if (String::class.java == type) {
            return Converter<String, RequestBody> { value -> RequestBody.create(MEDIA_TYPE, value) }
        }
        return null
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("text/plain")
    }
}