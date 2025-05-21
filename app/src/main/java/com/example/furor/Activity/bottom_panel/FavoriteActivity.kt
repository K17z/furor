package com.example.furor.activity.bottom_panel

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.furor.model.ItemsModel
import com.example.furor.R
import com.example.project1762.Helper.ManagmentFavorite
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import com.example.furor.activity.BaseActivity
import com.example.furor.activity.DetailActivity

class FavoriteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val favoriteManager = ManagmentFavorite(this)

        setContent {
            val favoriteItems = remember { mutableStateOf(favoriteManager.getFavoriteList()) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Назад",
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable { finish() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Избранное",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (favoriteItems.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Список избранного пуст", fontSize = 18.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        itemsIndexed(favoriteItems.value) { index, item ->
                            FavoriteItemCard(item = item, onRemove = {
                                favoriteManager.removeFavorite(item)
                                favoriteItems.value = favoriteManager.getFavoriteList()
                            }, onClick = {
                                val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
                                intent.putExtra("object", item)
                                startActivity(this@FavoriteActivity, intent, null)
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(item: ItemsModel, onRemove: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.picUrl.firstOrNull(),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.price}₽", color = Color(0xFF8B4513))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Button(
            onClick = onRemove,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000)),
            shape = RoundedCornerShape(8.dp),
        ) {
            Text("Удалить", color = Color.White)
        }
    }
}
