package com.example.furor.Activity

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.furor.R
import com.example.furor.utils.mainFieldStyle
import com.example.furor.utils.makeToast

class RegistrationActivity : AppCompatActivity() {

    private lateinit var context: RegistrationActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreetingInRegisterActivity() //Инициализируем пользователя
        }

    }

    @Composable
    fun GreetingInRegisterActivity() {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                val phoneField = mainFieldStyle(
                    labelText = "Номер телефона",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(8.dp))
                val passwordField = mainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(8.dp))
                val rePasswordField = mainFieldStyle(
                    labelText = "Повторите пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(120.dp))
                Button(
                    onClick = {
                        if (phoneField == "") {
                            makeToast("Введите номер телефона!", context)
                        } else if (passwordField == "") {
                            makeToast("Придумайте пароль", context)
                        } else if (rePasswordField != passwordField) {
                            makeToast("Проверьте повтореный пароль", context)
                        } else {
//                        phoneNumberFromSignUp = phoneField
//                        password = passwordField
//                        sign_in = false
//                        authUser(context, phoneNumberFromSignUp, callBack)

                        }
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }
}


