package com.appbenefy.sueldazo.core.base

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.merckers.asesorcajero.core.language.MerckersContextWrapper
import com.merckers.core.exception.Failure
import com.appbenefy.sueldazo.R
import com.appbenefy.sueldazo.core.AndroidApplication
import com.appbenefy.sueldazo.core.di.ApplicationComponent
import com.appbenefy.sueldazo.utils.Global
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Base Activity class with helper methods for handling fragment transactions and back button
 * events.
 *
 * @see AppCompatActivity
 */
abstract class BaseActivity : AppCompatActivity() {
    private var toolbarId: Toolbar? = null

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (application as AndroidApplication).appComponent
    }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    abstract fun layoutId(): Int
    open fun isDialog() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        toolbarId = findViewById(R.id.mainToolbar)
        if (toolbarId != null) setSupportActionBar(toolbarId)
//        if (fragment() != null) addFragment(savedInstanceState)
    }

    fun hideToolbar() { toolbarId?.visibility = View.GONE }

//    override fun onBackPressed() {
//        if(fragment() != null)
//            (supportFragmentManager.findFragmentById(id.fragmentContainer) as BaseFragment).onBackPressed()
//        super.onBackPressed()
//    }

//    private fun addFragment(savedInstanceState: Bundle?) =
//        savedInstanceState ?: supportFragmentManager.inTransaction {
//            add(
//                id.fragmentContainer, fragment()!!
//            )
//        }

    abstract fun fragment(): BaseFragment?

    override fun attachBaseContext(newBase: Context) {
       val context = MerckersContextWrapper.wrap(newBase, Global.defLocale?.language)
       super.attachBaseContext(context)
    }

    override fun getTheme(): Resources.Theme? {
        return try {
            val theme = super.getTheme()
            theme.applyStyle(Global.getDefaultTheme(this), true)
//            if(isDialog()) theme.applyStyle(R.style.merckerDialog, true)
            theme
        } catch (e: Exception) {
            null
        }
    }


    inline fun <reified T : BaseActivity> callIntent(requestCode: Int? = null, noinline body: (Intent.() -> Unit)? = null) =
        callIntent(T::class, requestCode, body)


    fun <T : BaseActivity> callIntent(cls: KClass<T>, requestCode: Int?, body: (Intent.() -> Unit)?) {
        val intent = Intent(this, cls.java)
        if (body != null) { intent.body() }
        if(requestCode != null)
            startActivityForResult(intent, requestCode)
        else
            startActivity(intent)
    }

    fun configureToolbar(@StringRes title: Int?, @IdRes toolbarId: Int) {
        if (title != null) {
            setTitle(title)
        }
        val myToolbar = findViewById<Toolbar>(toolbarId)
        setSupportActionBar(myToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
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

    }

    companion object {
        inline fun <reified T> callingIntent(context: Context, activity: T) =
            Intent(context, T::class.java)
    }
}
