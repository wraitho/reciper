package com.peeeq.reciper.network

import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Retrofit 2 has no [RetrofitError](http://static.javadoc.io/com.squareup.retrofit/retrofit/1.9.0/retrofit/RetrofitError.html)
 * This way we can still use it, and with the usage of [getErrorBodyAs][.getErrorBodyAs] method
 * we are able to easily convert the body to the desired error class.
 */
class RetrofitException internal constructor(message: String?, exception: Throwable?,

                                             /** The request URL which produced the error.  */
                                             val url: String?,
                                             /** Response object containing status code, headers, body, etc.  */
                                             val response: Response<*>?,
                                             /** The event kind which triggered this error.  */
                                             val kind: Kind,
                                             /** The Retrofit this request was executed on  */
                                             val retrofit: Retrofit) : RuntimeException(message, exception) {

    /** Identifies the event kind which triggered a [RetrofitException].  */
    enum class Kind {
        /** An [IOException] occurred while communicating to the server.  */
        NETWORK,
        /** A non-200 HTTP status code was received from the server.  */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<T>): T? {
        if (response?.errorBody() == null) {
            return null
        }
        val converter = retrofit.responseBodyConverter<T>(type, arrayOfNulls(0))
        return converter.convert(response.errorBody())
    }

    companion object {
        @JvmStatic private fun composeMessage(response: Response<*>, url: String) =
                "response code: ${response.code()}, response message: ${response.message()}, request url: $url"

        @JvmStatic fun httpError(url: String, response: Response<*>, retrofit: Retrofit): RetrofitException =
                RetrofitException(composeMessage(response, url), null, url, response, Kind.HTTP, retrofit)

        @JvmStatic fun networkError(exception: IOException, retrofit: Retrofit): RetrofitException =
                RetrofitException(exception.message, exception, null, null, Kind.NETWORK, retrofit)

        @JvmStatic fun unexpectedError(exception: Throwable, retrofit: Retrofit): RetrofitException =
                RetrofitException(exception.message, exception, null, null, Kind.UNEXPECTED, retrofit)
    }
}
