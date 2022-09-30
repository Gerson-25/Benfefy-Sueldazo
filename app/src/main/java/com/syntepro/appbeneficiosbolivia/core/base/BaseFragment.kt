/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.syntepro.appbeneficiosbolivia.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.merckers.core.exception.Failure
import com.syntepro.appbeneficiosbolivia.core.di.ApplicationComponent
import com.syntepro.appbeneficiosbolivia.R
import com.syntepro.appbeneficiosbolivia.core.AndroidApplication
import com.syntepro.appbeneficiosbolivia.core.app.ErrorMessage
import javax.inject.Inject

/**
 * Base Fragment class with helper methods for handling views and back button events.
 *
 * @see Fragment
 */
abstract class BaseFragment : Fragment() {

    abstract fun layoutId(): Int

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as AndroidApplication).appComponent
    }

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(layoutId(), container, false)


    open fun onBackPressed() {}

    internal fun firstTimeCreated(savedInstanceState: Bundle?) = savedInstanceState == null

    internal fun showProgress() = progressStatus(View.VISIBLE)

    internal fun hideProgress() = progressStatus(View.GONE)

    private fun progressStatus(viewStatus: Int) {
        val pg = view?.findViewById<LinearLayout>(R.id.progressId)
        with(activity) { if (this is BaseActivity) pg?.visibility = viewStatus }
    }

//    internal fun notify(@StringRes message: Int) =
//            Snackbar.make(viewContainer, message, Snackbar.LENGTH_SHORT).show()

//    internal fun notifyWithAction(@StringRes message: Int, @StringRes actionText: Int, action: () -> Any) {
//        val snackBar = Snackbar.make(viewContainer, message, Snackbar.LENGTH_INDEFINITE)
//        snackBar.setAction(actionText) { _ -> action.invoke() }
//        snackBar.setActionTextColor(ContextCompat.getColor(appContext, R.color.white))
//        snackBar.show()
//    }
//
//    internal fun notifyWithAction(message: String, @StringRes actionText: Int, action: () -> Any) {
//        val snackBar = Snackbar.make(viewContainer, message, Snackbar.LENGTH_INDEFINITE)
//        snackBar.setAction(actionText) { _ -> action.invoke() }
//        snackBar.setActionTextColor(ContextCompat.getColor(appContext, R.color.white))
//        snackBar.show()
//    }

    open fun handleFailure(failure: Failure?) {
        val view  = view
        ErrorMessage.notifyWithAction(view!!, failure!!.getMessage() ?: "", R.string.action_close, {})
    }

//    private fun renderFailure(message: String?) {
//        hideProgress()
//        notifyWithAction(message ?: "", R.string.action_close, {})
//    }
}
