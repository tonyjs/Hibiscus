package com.tonyjs.hibiscus.data.network.dto

import com.google.gson.annotations.SerializedName

data class CreateAccountResponse(@SerializedName("ok") val success: Boolean? = true,
                                 @SerializedName("result") val account: AccountDTO?)
