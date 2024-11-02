package com.example.dm125.repository

import com.example.dm125.entity.Task

data class ResponseDto<T> (
    val value: T? = null,
    val isError: Boolean = false
)