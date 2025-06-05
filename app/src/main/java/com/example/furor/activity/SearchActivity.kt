package com.example.furor.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.furor.model.ItemsModel
import com.google.firebase.database.*
import android.content.Intent
import com.example.furor.R
import com.example.furor.activity.DetailActivity

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun SearchScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<ItemsModel>() }
    var isLoading by remember { mutableStateOf(false) }

    fun performSearch(queryText: String) {
        isLoading = true
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                searchResults.clear()
                snapshot.children.mapNotNull { it.getValue(ItemsModel::class.java) }
                    .filter { it.title.contains(queryText, ignoreCase = true) }
                    .also { searchResults.addAll(it) }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Верхняя панель с отступом вниз
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .offset(y = 16.dp) // <<< отступ вниз
        ) {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
            }
            Spacer(modifier = Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = {
                    query = it
                    performSearch(it)
                },
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                textStyle = TextStyle(fontSize = 18.sp, color = Color.Black),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("Поиск...", color = Color.Gray, fontSize = 18.sp)
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Список результатов
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (searchResults.isEmpty() && query.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Ничего не найдено", fontSize = 18.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(searchResults) { item ->
                    SearchItemCard(item = item, onClick = {
                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("object", item)
                        context.startActivity(intent)
                    })
                }
            }
        }
    }
}

@Composable
fun SearchItemCard(item: ItemsModel, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.picUrl.firstOrNull(),
            contentDescription = item.title,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(item.title, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.price}₽", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
