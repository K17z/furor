package com.example.furor.helper

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.ColumnScope
import com.example.furor.activity.MainActivity
import com.example.furor.activity.login_regestration.RegistrationActivity
import com.example.furor.utils.goTo
import com.example.furor.utils.makeToast
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.auth

fun ColumnScope.authUser(settings: ActionCodeSettings, email: String, context: RegistrationActivity) {
    Firebase.auth.sendSignInLinkToEmail(email, settings)
        .addOnCompleteListener { task ->
            when (task.isSuccessful) {
                true -> {
                    Log.d("myTAG", "Email sent.")
                    makeToast("Регистрациия прошла успешно", context)
                    goTo(MainActivity::class.java, context, email)
                }
                false -> makeToast("Ошибка регистрации", context)
            }
        }
}