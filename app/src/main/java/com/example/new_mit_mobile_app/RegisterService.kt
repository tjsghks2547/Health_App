package com.example.new_mit_mobile_app

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.*

interface registerService{

    @POST("/Registered_Person/")
    fun registerData(@Body requestBody: RequestBody): Call<ResponseBody>

}