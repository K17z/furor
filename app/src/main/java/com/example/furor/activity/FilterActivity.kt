package com.example.furor.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.furor.R
import com.example.furor.model.ItemsModel
import com.example.furor.model.CategoryModel
import com.example.furor.viewModel.MainViewModel
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

class FilterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { FilterScreen { finish() } }
    }
}

@Composable
fun FilterScreen(onBack: () -> Unit) {
    val viewModel = MainViewModel()
    val allItems = remember { mutableStateListOf<ItemsModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    var minPrice by remember { mutableStateOf(0f) }
    var maxPrice by remember { mutableStateOf(10000f) }
    var currentRange by remember { mutableStateOf(0f..10000f) }

    // Фильтры
    var selectedColors by remember { mutableStateOf(listOf<String>()) }
    var selectedModels by remember { mutableStateOf(listOf<String>()) }
    var selectedCategories by remember { mutableStateOf(listOf<String>()) }
    var selectedRating by remember { mutableStateOf(0f) }
    var isFilterApplied by remember { mutableStateOf(false) }


    val Brown = Color(0xFF7C5C38)        // Основной коричневый
    val LightBrown = Color(0xFFFFFFFF)   // Молочный-бежевый
    val TextBlack = Color(0xFF272727)
    val Accent = Color(0xFFB9A078)       // Дополнительный светло-коричневый

    // --- Загрузка товаров и категорий ---
    LaunchedEffect(Unit) {
        viewModel.loadAllItems().observeForever {
            allItems.clear()
            allItems.addAll(it.filterNotNull())
            val prices = allItems.map { item -> item.price }
            minPrice = (prices.minOrNull() ?: 0.0).toFloat()
            maxPrice = (prices.maxOrNull() ?: 10000.0).toFloat()
            currentRange = minPrice..maxPrice
        }
        viewModel.loadCategory().observeForever {
            categories.clear()
            categories.addAll(it)
        }
    }

    // Опции фильтров
    val colorOptions = allItems.flatMap { it.color }.distinct().filter { it.isNotBlank() }
    val modelOptions = allItems.flatMap { it.model }.distinct().filter { it.isNotBlank() }
    val categoryOptions = categories.map { it.title }

    // Фильтрация товаров
    val filteredItems = allItems.filter { item ->
        (selectedColors.isEmpty() || item.color.any { it in selectedColors }) &&
                (selectedModels.isEmpty() || item.model.any { it in selectedModels }) &&
                (selectedCategories.isEmpty() || categories.find { it.id.toString() == item.categoryId }?.title in selectedCategories) &&
                (item.price in currentRange) &&
                (selectedRating == 0f || item.rating >= selectedRating)
    }

    Column(Modifier
        .fillMaxSize()
        .background(LightBrown)
        .padding(16.dp)) {

        // Кнопка "Назад" и заголовок
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = Brown,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBack() }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Фильтр товаров",
                fontSize = 22.sp,
                color = Brown,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
        Text("Цвета:", color = TextBlack)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            colorOptions.forEach { color ->
                CustomFilterButton(
                    text = color,
                    selected = selectedColors.contains(color),
                    selectedColor = Brown,
                    background = LightBrown,
                    onClick = {
                        selectedColors = if (selectedColors.contains(color)) selectedColors - color else selectedColors + color
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Размеры:", color = TextBlack)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            modelOptions.forEach { model ->
                CustomFilterButton(
                    text = model,
                    selected = selectedModels.contains(model),
                    selectedColor = Brown,
                    background = LightBrown,
                    onClick = {
                        selectedModels = if (selectedModels.contains(model)) selectedModels - model else selectedModels + model
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Категории:", color = TextBlack)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            categoryOptions.forEach { cat ->
                CustomFilterButton(
                    text = cat,
                    selected = selectedCategories.contains(cat),
                    selectedColor = Brown,
                    background = LightBrown,
                    onClick = {
                        selectedCategories = if (selectedCategories.contains(cat)) selectedCategories - cat else selectedCategories + cat
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Цена:", color = TextBlack)
        RangeSlider(
            value = currentRange,
            onValueChange = { currentRange = it },
            valueRange = minPrice..maxPrice,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = Brown,
                activeTrackColor = Brown
            )
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("от ${currentRange.start.roundToInt()}₽", color = TextBlack)
            Text("до ${currentRange.endInclusive.roundToInt()}₽", color = TextBlack)
        }

        Spacer(Modifier.height(8.dp))
        Text("Рейтинг:", color = TextBlack)
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            listOf(0f, 3f, 4f, 4.5f).forEach { rating ->
                CustomFilterButton(
                    text = if (rating == 0f) "Любой" else "${rating}+",
                    selected = selectedRating == rating,
                    selectedColor = Brown,
                    background = LightBrown,
                    onClick = { selectedRating = rating }
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Row {
            Button(
                onClick = { isFilterApplied = true },
                colors = ButtonDefaults.buttonColors(containerColor = Brown)
            ) { Text("Применить", color = Color.White) }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    selectedColors = emptyList()
                    selectedModels = emptyList()
                    selectedCategories = emptyList()
                    currentRange = minPrice..maxPrice
                    selectedRating = 0f
                    isFilterApplied = false
                },
                colors = ButtonDefaults.buttonColors(containerColor = Brown)
            ) { Text("Сбросить", color = Color.White) }
        }

        Spacer(Modifier.height(16.dp))
        Text("Результаты:", color = Brown)
        val context = LocalContext.current
        if (isFilterApplied) {
            LazyColumn(
                Modifier.weight(1f, fill = false).fillMaxWidth()
            ) {
                items(filteredItems.size) { idx ->
                    val item = filteredItems[idx]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                val intent = Intent(context, DetailActivity::class.java)
                                intent.putExtra("object", item)
                                context.startActivity(intent)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.picUrl.firstOrNull(),
                                contentDescription = item.title,
                                modifier = Modifier.size(70.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = item.title,
                                color = TextBlack,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                "Сначала выберите фильтры и нажмите Применить",
                color = Accent,
                fontSize = 14.sp
            )
        }
    }
}

// Кнопка фильтра с новой палитрой
@Composable
fun CustomFilterButton(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    background: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected) selectedColor else background,
        shadowElevation = 4.dp,
        modifier = Modifier
            .padding(end = 10.dp, bottom = 6.dp)
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 18.dp)
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else Color(0xFF272727),
                fontSize = 16.sp
            )
        }
    }
}
