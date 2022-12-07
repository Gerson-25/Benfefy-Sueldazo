package com.appbenefy.sueldazo.utils

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

fun TextView.Underline(context: Context, text: String) {
    val mSpannableString = SpannableString(text)
    mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)
    setText(mSpannableString)
}