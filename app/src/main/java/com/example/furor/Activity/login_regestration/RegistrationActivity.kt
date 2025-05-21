package com.example.furor.activity.login_regestration

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furor.helper.authUser
import com.example.furor.utils.mainFieldStyle
import com.example.furor.utils.makeToast
import com.google.firebase.auth.actionCodeSettings

class RegistrationActivity : AppCompatActivity() {
    //private lateinit var context: RegistrationActivity

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GreetingInRegisterActivity() //Инициализируем пользователя
        }

    }

    override fun onStart(){

        super.onStart()

    }

    @Composable
    fun GreetingInRegisterActivity() {
        val context: RegistrationActivity = this
        Column {
            Column(
                modifier = Modifier.Companion
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.Companion.CenterHorizontally
            )
            {
                val emailField = mainFieldStyle(
                    labelText = "Почта ",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.Companion.padding(8.dp))
                val passwordField = mainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.Companion.padding(8.dp))
                val rePasswordField = mainFieldStyle(
                    labelText = "Повторите пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.Companion.padding(120.dp))
                Button(
                    onClick = {
                        if (emailField == "") {
                            makeToast("Введите почту!", context)
                        } else if (passwordField == "") {
                            makeToast("Придумайте пароль", context)
                        } else if (rePasswordField != passwordField) {
                            makeToast("Проверьте повтореный пароль", context)
                        } else {
                            val actionCodeSettings = actionCodeSettings {
                                // URL you want to redirect back to. The domain (www.example.com) for this
                                // URL must be whitelisted in the Firebase Console.
                                url =
                                    "https://gtyjh-395ef.firebaseapp.com/__/auth/action?mode=action&oobCode=code"
                                // This must be true
                                handleCodeInApp = true
                                iosBundleId = "com.example.ios"
                                setAndroidPackageName(
                                    "com.example.furor",
                                    true, // installIfNotAvailable
                                    "33", // minimumVersion
                                )
                            }
                            authUser(actionCodeSettings, emailField, context)
                        }
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }
}