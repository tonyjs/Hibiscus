package com.tonyjs.hibiscus.data.network.dto

import com.google.gson.annotations.SerializedName

data class AccountDTO(@SerializedName("short_name") val shortName: String,
                      @SerializedName("access_token") val accessToken: String)
