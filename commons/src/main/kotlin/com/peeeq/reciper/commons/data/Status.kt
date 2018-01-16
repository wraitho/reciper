package com.peeeq.reciper.commons.data

sealed class Status
class Success : Status()
class Failure : Status()
class Loading : Status()
