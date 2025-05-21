package com.example.furor.activity.component_list

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.furor.model.ItemsModel
import com.example.furor.R
import com.example.furor.viewModel.MainViewModel
import com.example.project1762.Helper.ManagmentFavorite
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.runtime.livedata.observeAsState
import com.example.furor.activity.BaseActivity
import com.example.furor.activity.DetailActivity


class ListItemsActivity : BaseActivity() {
    private val viewModel = MainViewModel()
    private var id: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra("id") ?: ""
        title = intent.getStringExtra("title") ?: ""

        setContent {
            ListItemScreen(
                title = title,
                onBackClick = { finish() },
                viewModel = viewModel,
                id = id
            )
        }
    }

    @Composable
    private fun ListItemScreen(
        title: String,
        onBackClick: () -> Unit,
        viewModel: MainViewModel,
        id: String
    ) {
        val context = LocalContext.current
        val favoriteManager = remember { ManagmentFavorite(context) }

        val items by viewModel.loadFiltered(id).observeAsState(emptyList())
        var isLoading by remember { mutableStateOf(true) }

        val favoriteStates = remember {
            mutableStateMapOf<String, Boolean>().apply {
                items.forEach { item ->
                    this[item.title] = favoriteManager.isFavorite(item)
                }
            }
        }

        LaunchedEffect(id) {
            viewModel.loadFiltered(id)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            ConstraintLayout(
                modifier = Modifier.padding(top = 36.dp, start = 16.dp, end = 16.dp)
            ) {
                val (backBtn, cartTxt) = createRefs()

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(cartTxt) { centerTo(parent) },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    text = title
                )

                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
                    items(items) { item ->
                        val isFavorite = favoriteStates[item.title] ?: false

                        FavoriteItemCard(
                            item = item,
                            isFavorite = isFavorite,
                            onFavoriteToggle = {
                                val newValue = !isFavorite
                                favoriteStates[item.title] = newValue

                                if (newValue) {
                                    favoriteManager.insertFavorite(item)
                                } else {
                                    favoriteManager.removeFavorite(item)
                                }
                            },
                            onClick = {
                                val intent = Intent(context, DetailActivity::class.java)
                                intent.putExtra("object", item)
                                context.startActivity(intent, null)
                            }
                        )
                    }
                }
            }
        }

        LaunchedEffect(items) {
            isLoading = items.isEmpty()
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: ItemsModel,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.picUrl.firstOrNull(),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .clickable { onClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${item.price}₽", color = Color.Gray)
        }

        IconButton(onClick = onFavoriteToggle) {
            Icon(
                painter = painterResource(
                    if (isFavorite) R.drawable.ic_fav_filled
                    else R.drawable.ic_fav_border
                ),
                contentDescription = null,
                tint = if (isFavorite) Color.Red else Color.Gray
            )
        }
    }
}