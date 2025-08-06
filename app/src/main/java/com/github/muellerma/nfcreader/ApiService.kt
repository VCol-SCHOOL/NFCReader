package com.github.muellerma.nfcreader

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-Type: application/json", "X-API-KEY: tm4v1cb2el8hv3rf")
    @POST("tech-support/index.php")
    fun sendTechSupport(@Body request: TechSupportRequest): Call<TechSupportRequest>
}