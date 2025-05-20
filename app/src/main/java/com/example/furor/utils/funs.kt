package com.example.furor.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast

fun <templateActivity : Activity> goTo(
    activity: Class<templateActivity>,
    context: Context
) {
    val intent = Intent(context, activity)
    context.startActivity(intent)
}

fun makeToast(msg: String, context: Context) {
    Toast
        .makeText(context, msg, Toast.LENGTH_LONG)
        .show()
}