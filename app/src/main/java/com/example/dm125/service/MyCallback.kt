package com.example.dm125.service

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.dm125.entity.Task
import com.example.dm125.repository.ResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyCallback<T>(private val liveData: MutableLiveData<ResponseDto<T>>) : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            liveData.value = ResponseDto(value = response.body())
        } else {
            Log.e("server", "Erro do servidor")
            response.errorBody()?.let {  errorBody ->
                Log.e("server", errorBody.toString())
            }

            liveData.value = ResponseDto(isError = true)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("server", "Erro do servidor")
        t.message?.let {
            Log.e("server", "server exception: $it")
        }

        liveData.value = ResponseDto(isError = true)
    }
}