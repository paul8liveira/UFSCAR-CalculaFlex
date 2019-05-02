package br.blog.paul8liveira.calculaflex

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import br.blog.paul8liveira.calculaflex.utils.CalculaFlexTracker
import br.blog.paul8liveira.calculaflex.utils.ScreenMap
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

open class BaseActivity : AppCompatActivity() {

    open fun getScreenName(): String {
        return ScreenMap.getScreenNameBy(this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        Fabric.with(this, Crashlytics())
    }

    override fun onStart() {
        super.onStart()
        CalculaFlexTracker.trackScreen(this, getScreenName())
    }
}

