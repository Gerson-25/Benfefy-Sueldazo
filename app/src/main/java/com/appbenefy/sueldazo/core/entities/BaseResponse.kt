package com.appbenefy.sueldazo.core.entities

import androidx.annotation.Keep
import com.merckers.core.extension.empty

@Keep
data class BaseResponse<T>(
        val code: Int,
        val isSuccess: Boolean,
        val description: String,
        val data: T?
)
{
    companion object {
        fun <T> empty() = BaseResponse<T>(code = 0, isSuccess = false, description = String.empty(), null)
    }
}

