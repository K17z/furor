package com.example.furor.activity.bottom_panel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            YourProfile()
        }
    }
}

@Composable
fun YourProfile() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия
        Spacer(modifier = Modifier.padding(10.dp)) //Отступ
        Column(horizontalAlignment = Alignment.Start) {
            Row {
                Extracted("Ваше ФИО: ", "USER.fullname")
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

            Row {
                Extracted("Ваш номер телефона: ", "USER.phone")
            }
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(bottom = 10.dp)) //Линия

        }


    }
}

@Composable
private fun Extracted(string: String, info: String) {
    Spacer(modifier = Modifier.width(4.dp))
    androidx.compose.material3.Text(text = string, textAlign = TextAlign.Start)
    androidx.compose.material3.Text(text = info, textAlign = TextAlign.Start)
}
