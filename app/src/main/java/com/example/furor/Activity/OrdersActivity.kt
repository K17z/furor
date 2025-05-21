package com.example.furor.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furor.R
import com.example.project1762.Helper.ManagmentCart
import com.example.furor.Model.ItemsModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.shape.RoundedCornerShape



class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrdersScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun OrdersScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val managmentCart = remember { ManagmentCart(context) }
    val orders = remember { mutableStateOf(managmentCart.getOrders()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Назад",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Ваши заказы",
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp
            )
        }



        if (orders.value.isEmpty()) {
            Text(
                text = "Пока нет заказов.",
                fontSize = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(orders.value) { item ->
                    OrderItem(item)
                }
            }
        }
    }
}

@Composable
fun OrderItem(item: ItemsModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = item.title,
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = item.title, fontSize = 16.sp, color = Color.Black)
            Text(text = "Количество: ${item.numberInCart}", fontSize = 14.sp, color = Color.DarkGray)
            Text(text = "Сумма: ${item.numberInCart * item.price}₽", fontSize = 14.sp, color = Color.DarkGray)
        }
    }
}
