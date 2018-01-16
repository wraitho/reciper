package com.peeeq.reciper.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.jakewharton.rxbinding2.view.clicks
import com.peeeq.reciper.commons.data.Fail
import com.peeeq.reciper.commons.data.NetworkError
import com.peeeq.reciper.commons.data.ServerError
import com.peeeq.reciper.commons.data.UnknownError
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.view_error.view.*

class ErrorView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attributeSet, defStyleAttr) {

    private lateinit var error: Error

    init {
        LayoutInflater.from(context).inflate(R.layout.view_error, this, true)
    }

    /**
     * ErrorContent solution is temporary, normally these would go to strings.xml for localisation purposes.
     */
    enum class ErrorContent(val title: String, val description: String) {
        NETWORK_ERROR("Network Error", "Something happened during connecting to the internet, please check your connection"),
        SERVER_ERROR("Server Error", "Unfortunately something went wrong on our server side, please check back later"),
        UNKNOWN_ERROR("Unknown Error", "Opps, something went wrong. :'(")
    }

    data class Error(val errorContent: ErrorContent, val consumer: Consumer<Unit>)

    // todo figure out why this is not working.
    fun setFailure(fail: Fail, consumer: Consumer<Unit>) {

        error = when (fail) {
            is NetworkError -> Error(ErrorContent.NETWORK_ERROR, consumer)
            is ServerError -> Error(ErrorContent.SERVER_ERROR, consumer)
            is UnknownError -> Error(ErrorContent.UNKNOWN_ERROR, consumer)
        }

        errorTitle.text = error.errorContent.title
        errorDescription.text = error.errorContent.description
        errorAction.clicks().subscribe(error.consumer)
    }

}