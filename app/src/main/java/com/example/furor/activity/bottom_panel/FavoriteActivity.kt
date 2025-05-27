package com.example.furor.activity.bottom_panel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import com.example.furor.model.ItemsModel
import com.example.furor.utils.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.compose.ui.text.font.FontWeight

class FavoriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FavoriteScreen(onBack = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val favorites = remember { mutableStateListOf<ItemsModel>() }
    var isLoading by remember { mutableStateOf(true) }

    // Слушаем /favorites/{uid}
    LaunchedEffect(uid) {
        if (uid == null) {
            makeToast("Сначала войдите в аккаунт", context)
            isLoading = false
            return@LaunchedEffect
        }
        val ref = FirebaseDatabase.getInstance()
            .getReference("favorites")
            .child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favorites.clear()
                snapshot.children.mapNotNull { it.getValue(ItemsModel::class.java) }
                    .also { favorites.addAll(it) }
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                makeToast("Ошибка загрузки избранного: ${error.message}", context)
                isLoading = false
            }
        })
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Избранное", fontSize = 20.sp) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        )
    }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                favorites.isEmpty() -> Text(
                    "Список избранного пуст",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favorites) { item ->
                        FavoriteItemCard(
                            item = item,
                            onRemove = {
                                // удаляем из /favorites/{uid}
                                FirebaseDatabase.getInstance()
                                    .getReference("favorites")
                                    .child(uid!!)
                                    .orderByChild("title")
                                    .equalTo(item.title)
                                    .addListenerForSingleValueEvent(object: ValueEventListener{
                                        override fun onDataChange(s: DataSnapshot) {
                                            s.children.forEach { it.ref.removeValue() }
                                        }
                                        override fun onCancelled(e: DatabaseError) {}
                                    })
                            },
                            onClick = {
                                // ...ваш переход в DetailActivity
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: ItemsModel,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(item.picUrl.firstOrNull()).build(),
            contentDescription = item.title,
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("${item.price}₽", fontSize = 14.sp, color = Color.Gray)
        }
        Button(
            onClick = onRemove,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Удалить", color = Color.White)
        }
    }
}
