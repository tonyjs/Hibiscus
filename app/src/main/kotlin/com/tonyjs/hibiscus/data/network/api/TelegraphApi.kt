package com.tonyjs.hibiscus.data.network.api

import com.tonyjs.hibiscus.data.network.dto.CreateAccountResponse
import com.tonyjs.hibiscus.data.network.dto.UploadImageResponse
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface TelegraphApi {

    companion object {
        const val URL = "https://api.telegra.ph"
    }

    @GET("$URL/createAccount")
    fun createAccount(@Query("short_name") shortName: String): Single<CreateAccountResponse>

    //TODO
    @GET("$URL/createPage")
    fun createPage(@Query("access_token") token: String,
                   @Query("title", encoded = true) title: String,
                   @Query("author_name", encoded = true) authorName: String,
                   @Query("content", encoded = true) content: String,
                   @Query("return_content") returnContent:Boolean = true): Completable

    //TODO
    @Multipart
    @POST("http://telegra.ph/upload/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Single<List<UploadImageResponse>>

}