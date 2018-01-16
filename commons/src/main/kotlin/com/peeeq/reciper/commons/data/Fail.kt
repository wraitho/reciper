package com.peeeq.reciper.commons.data

sealed class Fail(val throwable: Throwable)
class NetworkError(throwable: Throwable) : Fail(throwable)
class ServerError(throwable: Throwable) : Fail(throwable)
class UnknownError(throwable: Throwable) : Fail(throwable)