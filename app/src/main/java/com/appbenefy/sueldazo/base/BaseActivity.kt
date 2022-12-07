package com.appbenefy.sueldazo.base

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.merckers.core.exception.Failure
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.core.app.ErrorMessage
import com.appbenefy.sueldazo.core.di.ApplicationComponent
import javax.inject.Inject
import kotlin.reflect.KClass

open class BaseActivity: AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }
    private var dialog: AlertDialog? = null
    //    private var prefs: SharedPreferences? = null
    private var mProgressId: LinearLayout? = null
    private var mRefreshLayout: SwipeRefreshLayout?= null
    private var mEmptyId: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        configStart()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        configStart()
    }

    private fun configStart() {
//        val swipe = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
//        if(swipe != null) mRefreshLayout = swipe
//        errorLayoutId = findViewById(R.id.errorLayoutId)
//        errorMessageId = findViewById(R.id.errorMessageId)
//        errorRetryId = findViewById(R.id.errorRetryId)
//        errorCloseId = findViewById(R.id.errorCloseId)
        mProgressId = findViewById(R.id.progressId)
        mEmptyId = findViewById(R.id.emptyId)
//        mRefreshLayout?.setOnRefreshListener { getData() }
    }

    open fun stopRefreshing() {}

    open fun showEmptyLayout(show: Boolean) {
        mProgressId?.visibility = View.GONE
        stopRefreshing()
        mEmptyId?.visibility = if(show) View.VISIBLE else View.GONE
    }

    open fun showProgress(show: Boolean) {
        mProgressId?.visibility = if(show) View.VISIBLE else View.GONE
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    inline fun <reified T : ViewModel> viewModel(
            factory: ViewModelProvider.Factory,
            body: T.() -> Unit
    ): T {
        val vm = ViewModelProviders.of(this, factory)[T::class.java]
        vm.body()
        return vm
    }

    open fun handleError(failure: Failure?) {
        val parentLayout = findViewById<View>(android.R.id.content)
        ErrorMessage.notifyWithAction(parentLayout, failure!!.getMessage() ?: "", R.string.action_close, {})
    }

    inline fun <reified T : BaseActivity> callIntent(
            requestCode: Int? = null,
            noinline body: (Intent.() -> Unit)? = null
    ) =
            callIntent(T::class, requestCode, body)

    fun <T : BaseActivity> callIntent(cls: KClass<T>, requestCode: Int?, body: (Intent.() -> Unit)?) {
        val intent = Intent(this, cls.java)
        if (body != null) { intent.body() }
        if(requestCode != null)
            startActivityForResult(intent, requestCode)
        else
            startActivity(intent)
    }


}