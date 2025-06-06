package com.example.furor.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.furor.R
import com.example.furor.Helper.CartActivity
import com.example.furor.activity.bottom_panel.FavoriteActivity
import com.example.furor.activity.bottom_panel.OrdersActivity
import com.example.furor.activity.bottom_panel.ProfileActivity
import com.example.furor.activity.component_list.ListItems
import com.example.furor.activity.component_list.ListItemsActivity
import com.example.furor.model.CategoryModel
import com.example.furor.model.ItemsModel
import com.example.furor.model.SliderModel
import com.example.furor.utils.goTo
import com.example.furor.utils.makeToast
import com.example.furor.viewModel.MainViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.ContextCompat.startActivity
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainActivityScreen {
                startActivity(Intent(this, CartActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        when {
            user == null -> {
                // Если не залогинен — отправляем на экран входа
                goTo(com.example.furor.activity.login_regestration.LoginActivity::class.java, this)
                finish()
            }
            !user.isEmailVerified -> {
                // Если почта не подтверждена — показываем сообщение и выходим
                makeToast("Пожалуйста, подтвердите почту через ссылку из письма.", this)
                auth.signOut()
                goTo(com.example.furor.activity.login_regestration.LoginActivity::class.java, this)
                finish()
            }
            // иначе оставляем пользователя в MainActivity
        }
    }
}


@Composable
fun MainActivityScreen(onCartClick: () -> Unit) {
    val viewModel = MainViewModel()

    val banners = remember { mutableStateListOf<SliderModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    val popular = remember { mutableStateListOf<ItemsModel>() }

    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showPopularLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever {
            banners.clear()
            banners.addAll(it)
            showBannerLoading = false
        }
        viewModel.loadCategory().observeForever {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading = false
        }
        viewModel.loadpopular().observeForever {
            popular.clear()
            popular.addAll(it)
            showPopularLoading = false
        }
    }

    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (scrollList, bottomMenu) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            item {
                HeaderRow()
            }
            item {
                if (showBannerLoading) {
                    LoadingBox(height = 200.dp)
                } else {
                    Banners(banners)
                }
            }
            item {
                SectionTitle("Официальные бренды", "")
            }
            item {
                if (showCategoryLoading) {
                    LoadingBox(height = 50.dp)
                } else {
                    CategoryList(categories)
                }
            }
            item {
                SectionTitle("Самые популярные", "")
            }
            item {
                if (showPopularLoading) {
                    LoadingBox(height = 200.dp)
                } else {
                    ListItems(popular)
                }
            }
        }

        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu) {
                    bottom.linkTo(parent.bottom)
                },
            onItemClick = onCartClick
        )
    }
}

@Composable
private fun HeaderRow() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 70.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Добро пожаловать", color = Color.Black)
            Text(
                text = "",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Row {
            Icon(
                painterResource(id = R.drawable.search_icon),
                contentDescription = "Поиск",
                tint = Color.Unspecified,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, SearchActivity::class.java))
                }
            )
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFF0F0F0), CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        context.startActivity(Intent(context, FilterActivity::class.java))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Фильтр",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingBox(height: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CategoryList(categories: List<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val context = LocalContext.current
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
    ) {
        items(categories.size) { index ->
            CategoryItem(
                item = categories[index],
                isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(context, ListItemsActivity::class.java).apply {
                            putExtra("id", categories[index].id.toString())
                            putExtra("title", categories[index].title)
                        }
                        startActivity(context, intent, null)
                    }, 500)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(item: CategoryModel, isSelected: Boolean, onItemClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = item.picUrl,
            contentDescription = item.title,
            modifier = Modifier
                .size(if (isSelected) 60.dp else 50.dp)
                .background(
                    color = if (isSelected) colorResource(R.color.fiol) else colorResource(R.color.Sandyellow),
                    shape = RoundedCornerShape(100.dp)
                ),
            contentScale = ContentScale.Inside,
            colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Black)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            color = colorResource(R.color.Brown),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SectionTitle(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = actionText, color = colorResource(R.color.fiol))
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Banners(banners: List<SliderModel>) {
    AutoSlidingCarousel(banners = banners)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier.padding(top = 16.dp),
    pagerState: PagerState = remember { PagerState() },
    banners: List<SliderModel>
) {
    Column(modifier = modifier.fillMaxSize()) {
        HorizontalPager(count = banners.size, state = pagerState) { page ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(banners[page].url)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp)
                    .height(150.dp)
            )
        }
        DotIndicator(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .align(Alignment.CenterHorizontally),
            totalDots = banners.size,
            selectedIndex = pagerState.currentPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun DotIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = colorResource(R.color.Brown),
    unSelectedColor: Color = colorResource(R.color.grey),
    dotSize: Dp
) {
    LazyRow(modifier = modifier.wrapContentSize()) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )
            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(size: Dp, color: Color) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun BottomMenu(modifier: Modifier, onItemClick: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
            .background(colorResource(R.color.Brown), shape = RoundedCornerShape(10.dp)),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        BottomMenuItem(
            icon = painterResource(R.drawable.btn_2),
            text = "Корзина",
            onItemClick = onItemClick
        )
        BottomMenuItem(icon = painterResource(R.drawable.btn_3), text = "Избранное") {
            goTo(FavoriteActivity::class.java, context)
        }
        BottomMenuItem(icon = painterResource(R.drawable.btn_4), text = "Заказы") {
            goTo(OrdersActivity::class.java, context)
        }
        BottomMenuItem(icon = painterResource(R.drawable.btn_5), text = "Профиль") {
            goTo(ProfileActivity::class.java, context)
        }
    }
}

@Composable
fun BottomMenuItem(icon: Painter, text: String, onItemClick: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .height(70.dp)
            .clickable { onItemClick?.invoke() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = text, tint = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text, color = Color.White, fontSize = 10.sp)
    }
}
@Composable
fun FilterBottomSheet(
    colors: List<String>,
    models: List<String>,
    categories: List<String>,
    selectedColors: List<String>,
    selectedModels: List<String>,
    selectedCategories: List<String>,
    onColorSelected: (String) -> Unit,
    onModelSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text("Фильтр", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(16.dp))
        Text("Цвета:", fontWeight = FontWeight.SemiBold)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            colors.forEach { color ->
                FilterButton(
                    text = color,
                    selected = selectedColors.contains(color),
                    onClick = { onColorSelected(color) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Размер:", fontWeight = FontWeight.SemiBold)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            models.forEach { model ->
                FilterButton(
                    text = model,
                    selected = selectedModels.contains(model),
                    onClick = { onModelSelected(model) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Категория:", fontWeight = FontWeight.SemiBold)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            categories.forEach { category ->
                FilterButton(
                    text = category,
                    selected = selectedCategories.contains(category),
                    onClick = { onCategorySelected(category) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = onApply, modifier = Modifier.weight(1f)) { Text("Применить") }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onClear, modifier = Modifier.weight(1f)) { Text("Сбросить") }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Закрыть")
        }
    }
}
@Composable
fun FilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF7E57C2) else Color(0xFFF6EFD9),
            contentColor = if (selected) Color.White else Color.Black
        ),
        modifier = modifier.padding(end = 6.dp, bottom = 4.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}
