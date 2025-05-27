package com.example.furor.activity.bottom_panel

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furor.activity.login_regestration.LoginActivity
import com.example.furor.utils.goTo
import com.example.furor.utils.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val uid = auth.currentUser?.uid.orEmpty()
    private val userEmail = auth.currentUser?.email.orEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(
                email = userEmail,
                auth = auth,
                dbRef = dbRef,
                uid = uid,
                onSignOut = {
                    auth.signOut()
                    goTo(LoginActivity::class.java, this)
                    finish()
                }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    email: String,
    auth: FirebaseAuth,
    dbRef: DatabaseReference,
    uid: String,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    var fullname by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }

    // Загрузим имя и телефон из БД
    LaunchedEffect(uid) {
        dbRef.child("users").child(uid)
            .get()
            .addOnSuccessListener { snap ->
                fullname = snap.child("fullname").getValue(String::class.java).orEmpty()
                phone    = snap.child("phone").getValue(String::class.java).orEmpty()
                loading = false
            }
            .addOnFailureListener {
                makeToast("Ошибка загрузки профиля: ${it.message}", context)
                loading = false
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEFEF))
            .padding(16.dp)
    ) {
        // Кнопка "Назад" чуть ниже
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Назад",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 16.dp)          // << сдвиг вниз
                .clickable { (context as? Activity)?.finish() }
                .padding(8.dp)
        )

        // Заголовок опустили на те же 16.dp
        Text(
            text = "Профиль",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 16.dp)          // << сдвиг вниз
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Email (недоступно для редактирования)
            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            // Поле Имя с русской раскладкой
            OutlinedTextField(
                value = fullname,
                onValueChange = { fullname = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            // Поле Телефон (цифровая клавиатура)
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Телефон") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Кнопка Сохранить
            Button(
                onClick = {
                    val updates = mapOf<String, Any>(
                        "fullname" to fullname,
                        "phone"    to phone
                    )
                    dbRef.child("users").child(uid)
                        .updateChildren(updates)
                        .addOnSuccessListener {
                            makeToast("Профиль сохранён.", context)
                        }
                        .addOnFailureListener {
                            makeToast("Ошибка сохранения: ${it.message}", context)
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Сохранить")
            }

            // Кнопка Выйти
            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Выйти")
            }
        }
    }
}
