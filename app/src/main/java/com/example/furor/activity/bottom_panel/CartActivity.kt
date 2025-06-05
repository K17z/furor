package com.example.furor.activity.bottom_panel

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.furor.R
import com.example.furor.activity.BaseActivity
import com.example.furor.Helper.ManagmentCart
import com.example.furor.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val manager = ManagmentCart(this)
        setContent {
            CartScreen(
                managmentCart = manager,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun CartScreen(
    managmentCart: ManagmentCart,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Состояние корзины из Firebase
    val cartItems = remember { mutableStateListOf<ItemsModel>() }
    var tax by remember { mutableStateOf(0.0) }
    val delivery = 10.0

    // Подписка на узел "cart/{uid}"
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val ref = FirebaseDatabase.getInstance()
            .getReference("cart")
            .child(uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItems.clear()
                snapshot.children.mapNotNull {
                    it.getValue(ItemsModel::class.java)
                }.also { list -> cartItems.addAll(list) }
                tax = ((cartItems.sumOf { it.price * it.numberInCart }) * 0.02 * 100).toInt() / 100.0
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Не удалось загрузить корзину", Toast.LENGTH_SHORT).show()
            }
        })
    }

    BackHandler { onBackClick() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp) // только горизонтальные отступы
    ) {
        // Пробел сверху, чтобы заголовок и кнопка «назад» были чуть ниже
        Spacer(modifier = Modifier.height(32.dp))

        // Заголовок с кнопкой «назад»
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Ваша корзина",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(4f)
            )
            Spacer(Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp)) // отступ между заголовком и контентом

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Корзина пуста")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 0.dp)
            ) {
                items(cartItems) { item ->
                    CartItem(
                        item = item,
                        managmentCart = managmentCart
                    )
                }
            }
            CartSummary(
                itemTotal = cartItems.sumOf { it.price * it.numberInCart },
                tax = tax,
                delivery = delivery,
                managmentCart = managmentCart
            )
        }
    }
}

@Composable
fun CartItem(
    item: ItemsModel,
    managmentCart: ManagmentCart
) {
    val context = LocalContext.current

    // Берём актуальный список и позицию элемента
    val currentList = managmentCart.getListCart()
    val position = currentList.indexOfFirst { it.title == item.title }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Картинка товара
        AsyncImage(
            model = item.picUrl.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .background(Color.LightGray, RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.width(8.dp))

        // Текст (название, цена за единицу, итоговая цена)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(item.title)
            Text(
                text = "${item.price}₽",
                color = colorResource(R.color.Brown)
            )
            Text(
                text = "${item.price * item.numberInCart}₽",
                fontWeight = FontWeight.Bold
            )
        }

        // Блок с «–  count  +»
        Box(
            modifier = Modifier
                .width(100.dp)
                .background(colorResource(R.color.Brown), RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            managmentCart.minusItem(currentList, position)
                        }
                )
                Text(
                    text = "${item.numberInCart}",
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "+",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            managmentCart.plusItem(currentList, position)
                        }
                )
            }
        }
    }
}

@Composable
fun CartSummary(
    itemTotal: Double,
    tax: Double,
    delivery: Double,
    managmentCart: ManagmentCart
) {
    val context = LocalContext.current
    val total = itemTotal + tax + delivery

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        SummaryRow("Итоговая цена:", "$itemTotal₽")
        SummaryRow("Налог:", "$tax₽")
        SummaryRow("Доставка:", "$delivery₽")
        SummaryRow("Итог:", "$total₽")
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                managmentCart.placeOrder()
                Toast.makeText(context, "Заказ оформлен", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, OrdersActivity::class.java))
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.Brown)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Оформить заказ", fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label, Modifier.weight(1f), fontWeight = FontWeight.Bold)
        Text(value)
    }
}
