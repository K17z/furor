package com.example.furor.activity.login_regestration

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furor.utils.mainFieldStyle
import com.example.furor.utils.makeToast
import com.example.furor.utils.goTo
import com.example.furor.activity.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreen()
        }
    }

    override fun onStart() {
        super.onStart()
        Firebase.auth.currentUser
            ?.takeIf { it.isEmailVerified }
            ?.let {
                goTo(MainActivity::class.java, this)
                finish()
            }
    }

    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val emailField = mainFieldStyle(
                labelText = "Почта",
                enable    = true,
                maxLine   = 1
            ) {}
            Spacer(modifier = Modifier.height(12.dp))

            val passwordField = mainFieldStyle(
                labelText = "Пароль",
                enable    = true,
                maxLine   = 1
            ) {}
            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                when {
                    emailField.isBlank() ->
                        makeToast("Введите почту!", context)
                    passwordField.isBlank() ->
                        makeToast("Введите пароль!", context)
                    else -> {
                        Firebase.auth
                            .signInWithEmailAndPassword(emailField, passwordField)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = Firebase.auth.currentUser
                                    if (user?.isEmailVerified == true) {
                                        goTo(MainActivity::class.java, context)
                                        finish()
                                    } else {
                                        makeToast(
                                            "Подтвердите почту через письмо.",
                                            context
                                        )
                                        user
                                            ?.sendEmailVerification()
                                            ?.addOnCompleteListener { verifyTask ->
                                                if (verifyTask.isSuccessful) {
                                                    makeToast(
                                                        "Письмо повторно отправлено.",
                                                        context
                                                    )
                                                } else {
                                                    makeToast(
                                                        "Ошибка при отправке: " +
                                                                "${verifyTask.exception?.message}",
                                                        context
                                                    )
                                                }
                                            }
                                        Firebase.auth.signOut()
                                    }
                                } else {
                                    makeToast(
                                        "Ошибка входа: ${task.exception?.message}",
                                        context
                                    )
                                }
                            }
                    }
                }
            }) {
                Text(text = "Войти", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет аккаунта? Зарегистрироваться",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        goTo(RegistrationActivity::class.java, context)
                        finish()
                    }
            )
        }
    }
}
