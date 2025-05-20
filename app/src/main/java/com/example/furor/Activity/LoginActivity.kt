package com.example.furor.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.furor.Model.User
import com.example.furor.R
import com.example.furor.utils.Constants.NODE_USERS
import com.example.furor.utils.goTo
import com.example.furor.utils.mainFieldStyle
import com.example.furor.utils.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User
lateinit var UID: String //Уникальный индификационный номер

class LoginActivity : AppCompatActivity() {
    private lateinit var context: LoginActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InitUserLoginActivity() //Инициализируем пользователя
        }

    }

    @Composable
    private fun InitUserLoginActivity(){
        REF_DATABASE_ROOT
            .child(NODE_USERS)
            .child(UID)
            .addListenerForSingleValueEvent(
                object : ValueEventListener { //Один раз при запуске обновляем наши данные
                    override fun onDataChange(snapshot: DataSnapshot) {
                        USER = snapshot.getValue(User::class.java)
                            ?: User() //Получаем данные через переменную snapshot. Если будет null поле, то вы инициализируем пустым пользователем
                        if (AUTH.currentUser != null) { //Если пользователь уже есть
                            goTo(MainActivity::class.java, context)
                        }
                        else {
                            setContent{
                                GreetingInLoginActivity()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        makeToast("Ошибка входа", context)
                    }
                }
            )
    }


    @Composable
    fun GreetingInLoginActivity() {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 100.dp, 0.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                val phone = mainFieldStyle(
                    labelText = "Номер телефона",
                    enable = true,
                    maxLine = 1
                ) {}
                Spacer(modifier = Modifier.padding(8.dp))
                val password = mainFieldStyle(
                    labelText = "Пароль",
                    enable = true,
                    maxLine = 1
                ) {}

                Spacer(modifier = Modifier.padding(120.dp))
                Button(
                    onClick = {
                        var pattern = Regex("[^\\d+]")
                        var formattedPhone = phone.replace(pattern, "")
                        formattedPhone = if (!formattedPhone.startsWith("+")) "+$formattedPhone" else formattedPhone
                        pattern = Regex("(\\+\\d+)(\\d{3})(\\d{3})(\\d{4})")

                        formattedPhone = pattern.replace(formattedPhone) { match ->
                            "${match.groups[1]?.value}" +
                                    " ${match.groups[2]?.value}" +
                                    "-${match.groups[3]?.value}" +
                                    "-${match.groups[4]?.value}"
                        }
                        //checkPhone(/*formattedPhone*/phone, password)
                    }
                ) { Text("Sing in", fontSize = 18.sp) }

                Box(
                    contentAlignment = Alignment.BottomCenter, modifier = Modifier
                        .fillMaxSize()
                        .padding(0.dp, 0.dp, 0.dp, 40.dp)
                ) { SingUpInLoginActivity() }

            }
        }

    }

    @Composable
    fun SingUpInLoginActivity() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "У вас нет аккаунта?\nДавайте зарегистрируемся!",
                modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 35.dp, 0.dp)
            ) {
                Button(
                    onClick = {
                        goTo(RegistrationActivity::class.java, context)
                    }
                ) {
                    Text("Sing up", fontSize = 18.sp)
                }
            }
        }
    }
}

