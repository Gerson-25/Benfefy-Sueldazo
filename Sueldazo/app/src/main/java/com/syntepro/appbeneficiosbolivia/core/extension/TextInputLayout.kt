package com.merckers.asesorcajero.core.extension

import com.google.android.material.textfield.TextInputLayout


fun TextInputLayout.isNull(message: String): Boolean {
    if (editText?.text.isNullOrEmpty()) {
        this.error = message
        return true
    }
    return false
}