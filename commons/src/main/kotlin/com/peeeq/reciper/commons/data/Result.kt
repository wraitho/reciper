package com.peeeq.reciper.commons.data

data class Result<out T>(val status: Status, val data: T?, val error: Fail? = null) {
    companion object {
        @JvmStatic fun <T> success(data: T): Result<T> =
                Result(Success(), data, null)

        @JvmStatic @JvmOverloads fun <T> failure(error: Fail? = null, data: T? = null): Result<T> =
                Result(Failure(), data, error)

        @JvmStatic fun <T> loading(): Result<T> =
                Result(Loading(), null, null)
    }
}