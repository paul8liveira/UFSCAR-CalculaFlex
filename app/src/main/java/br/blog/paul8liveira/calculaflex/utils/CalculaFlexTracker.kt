package br.blog.paul8liveira.calculaflex.utils

import android.app.Activity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object CalculaFlexTracker {

    fun trackScreen(activity: Activity, screenName: String) {
        if (screenName != ScreenMap.SCREEN_NOT_TRACKING) {
            //Log.i("ANALYTICS", screenName)
            val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
            mFirebaseAnalytics.setCurrentScreen(activity, screenName, null)
        }
    }

    fun trackEvent(activity: Activity, bundle: Bundle) {
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        if(bundle.containsKey("EVENT_NAME")) {
            mFirebaseAnalytics.logEvent(bundle.getString("EVENT_NAME"), bundle);
        } else {
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }
}