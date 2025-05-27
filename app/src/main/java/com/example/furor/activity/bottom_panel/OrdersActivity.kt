package com.example.furor.activity.bottom_panel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import com.example.furor.model.OrderModel
import com.example.furor.model.ItemsModel
import com.example.furor.utils.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrdersScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val orders = remember { mutableStateListOf<OrderModel>() }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uid) {
        if (uid == null) {
            makeToast("Пользователь не авторизован", context)
            isLoading = false
            return@LaunchedEffect
        }
        val ref = FirebaseDatabase.getInstance()
            .getReference("orders")
            .child(uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orders.clear()
                for (orderSnap in snapshot.children) {
                    orderSnap.getValue(OrderModel::class.java)
                        ?.let { orders.add(it) }
                }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                makeToast("Ошибка загрузки заказов: ${error.message}", context)
                isLoading = false
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ваши заказы", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                orders.isEmpty() -> {
                    Text("Пока нет заказов", Modifier.align(Alignment.Center), fontSize = 16.sp)
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        itemsIndexed(orders) { index, order ->
                            OrderGroupCard(order = order, number = index + 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderGroupCard(order: OrderModel, number: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Заказ №$number", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(
                text = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    .format(Date(order.timestamp)),
                fontSize = 12.sp,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(8.dp))
            order.items.forEach { item ->
                OrderItem(item)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun OrderItem(item: ItemsModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.picUrl.firstOrNull())
                .build(),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = item.title, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Кол-во: ${item.numberInCart}", fontSize = 14.sp, color = Color.Gray)
            Text(text = "Сумма: ${item.numberInCart * item.price} ₽", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
