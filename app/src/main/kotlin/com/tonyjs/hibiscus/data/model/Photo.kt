package com.tonyjs.hibiscus.data.model

data class Photo(val id: Long,
                 val path: String,
                 var thumbPath: String?,
                 var width: Int,
                 var height: Int,
                 var orientation: Int,
                 val mimeType: String?)