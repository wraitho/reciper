package com.peeeq.reciper.commons.dispatcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import javax.inject.Inject
import android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation
import android.view.View


/**
 * Dispatcher is responsible to create URIs that are sent to other activities. An activity with a specific intent filter
 * then catch the appropriate intent and start an activity. This way the feature modules are independent from eachother.
 *
 * This class could be also placed in a separate 'dispatcher' module, but it's currently a really small app so no need for it.
 */
class Dispatcher @Inject constructor() {

    companion object {
        @JvmStatic val TAG = "Dispatcher"
        @JvmStatic val SCHEME = "reciper-dispatcher"
        @JvmStatic val AUTHORITY = "reciper.peeq.com"
        @JvmStatic val PATH_DETAILS = "details"
        @JvmStatic val QUERY_PARAM_RECIPE_ID= "recipeId"
    }

    fun goToDetails(context: Context, id: String, sharedElement: View? = null) {
        val uri = Uri.Builder()
                .scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_DETAILS)
                .appendQueryParameter(QUERY_PARAM_RECIPE_ID, id)
                .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.`package` = context.packageName

        Log.d(TAG, "Starting activity with dispatched intent: $uri")

        if (sharedElement != null) {
            val options = makeSceneTransitionAnimation(context as Activity, sharedElement, "image")
            context.startActivity(intent, options.toBundle())
        } else {
            context.startActivity(intent)
        }
    }
}
