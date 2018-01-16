package com.peeeq.reciper.network

class ApiKeyNotSetException: Exception("\"apiKey\" is not set, please register to food2fork.com and set the key in the NetworkModule")