package com.example.furor.Activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.furor.Activity.BaseActivity
import com.example.furor.Model.ItemsModel
import com.example.furor.R
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.example.project1762.Helper.ManagmentCart
import java.util.ArrayList
import android.widget.Toast
import android.content.Intent



class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            CartScreen ( ManagmentCart(this),
                onBackClick={
                    finish()
                })
        }
    }
}

fun calculatorCart(managmentCart: ManagmentCart,tax:MutableState<Double>){
    val percentTax = 0.02
    tax.value=Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0
}

@Composable
private fun CartScreen(
    managmentCart: ManagmentCart = ManagmentCart(LocalContext.current),
    onBackClick: () -> Unit
){
    val cartItems = remember { mutableStateOf(managmentCart.getListCart()) }
    val tax = remember { mutableStateOf(0.0) }
    calculatorCart(managmentCart, tax)
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        ConstraintLayout (modifier = Modifier.padding(top = 36.dp)){
            val (backBtn,cartTxt)= createRefs()
            Text(modifier = Modifier
                .fillMaxWidth()
                .constrainAs(cartTxt){centerTo(parent)}
                , text = "Ваша корзина",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp
            )
            Image(painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(backBtn){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }
        if (cartItems.value.isEmpty()){
            Text(text = "Корзина пуста", modifier = Modifier.align(Alignment.CenterHorizontally))
        }else{
            CartList(cartItems = cartItems.value, managmentCart){
                cartItems.value=managmentCart.getListCart()
                calculatorCart(managmentCart,tax)
            }
            CartSummary(
                itemTotal = managmentCart.getTotalFee(),
                tax = tax.value,
                delivery = 10.0,
                managmentCart = managmentCart
            )

        }
    }
}


@Composable
fun CartSummary(itemTotal: Double, tax: Double, delivery: Double, managmentCart: ManagmentCart) {
    val context = LocalContext.current

    val total=itemTotal + tax + delivery


    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ){
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top=16.dp)){
            Text(
                text = "Итоговая цена:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.Brown)
            )
            Text(text = "$itemTotal₽")
        }
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top=16.dp)){
            Text(
                text = "Налог:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.Brown)
            )
            Text(text = "$tax")
        }
        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(top=16.dp)){
            Text(
                text = "Доставка:",
                Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.Brown)
            )
            Text(text = "$delivery")
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ){
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(top=16.dp)){
                Text(
                    text = "Итог:",
                    Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.Brown)
                )
                Text(text = "$total")
            }
            Button(
                onClick = {
                    managmentCart.placeOrder()
                    Toast.makeText(context, "Заказ оформлен", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, OrdersActivity::class.java))
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.Brown)
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ){
                Text(
                    text = "Оформить заказ",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CartList(cartItems: ArrayList<ItemsModel>, managmentCart: ManagmentCart, onItemChange:() -> Unit)
{
    LazyColumn(Modifier.padding(top = 16.dp)){
        items(cartItems){ item ->
            CartItem(
                cartItems,
                item = item,
                managmentCart=managmentCart,
                onItemChange=onItemChange
            )
        }
    }
}

@Composable
fun CartItem(
    cartItems: ArrayList<ItemsModel>,
    item: ItemsModel,
    managmentCart: ManagmentCart,
    onItemChange: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        val (pic, titleTxt, feeEachItem,totalEachItem, Quantity) = createRefs()

        Image(
            painter = rememberAsyncImagePainter(item.picUrl[0]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(90.dp)
                .background(
                    colorResource(R.color.Brown),
                    shape = RoundedCornerShape(10.dp)
                )

                .constrainAs(pic){
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        Text(text = item.title,
            modifier = Modifier
                .constrainAs(titleTxt){
                    start.linkTo(pic.end)
                    top.linkTo(pic.top)
                }
                .padding(start = 8.dp, top = 8.dp)
        )
        Text(text="${item.price}₽", color = colorResource(R.color.Brown), modifier = Modifier
            .constrainAs(feeEachItem){
                start.linkTo(titleTxt.start)
                top.linkTo(titleTxt.bottom)
            }
            .padding(start = 8.dp, top = 8.dp)
        )
        Text(text="${item.numberInCart*item.price}₽",
            fontSize =18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .constrainAs(totalEachItem){
                    start.linkTo(titleTxt.start)
                    bottom.linkTo(pic.bottom)
                }
                .padding(start = 8.dp,)
        )
        ConstraintLayout(
            modifier = Modifier
                .width(100.dp)
                .constrainAs(Quantity){
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .background(
                    colorResource(R.color.Brown),
                    shape = RoundedCornerShape(100.dp)
                )
        ) {
            val (plusCartBtn, minusCartBtn, numberItemText) = createRefs()
            Text(text = item.numberInCart.toString(),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(numberItemText){
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
            Box(modifier = Modifier
                .padding(2.dp)
                .size(28.dp)
                .background(
                    colorResource(R.color.Brown),
                    shape = RoundedCornerShape(100.dp)
                )
                .constrainAs(plusCartBtn){
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .clickable {
                    managmentCart.plusItem(
                        cartItems,
                        cartItems.indexOf(item),
                        object :ChangeNumberItemsListener{
                            override fun onChanged() {
                                onItemChange()
                            }
                        }
                    )
                }
            ){
                Text(
                    text = "+",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .background(
                        colorResource(R.color.white),
                        shape = RoundedCornerShape(100.dp)
                    )
                    .constrainAs(minusCartBtn){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clickable {
                        managmentCart.minusItem(cartItems,
                            cartItems.indexOf(item),object :ChangeNumberItemsListener{
                                override fun onChanged() {
                                    onItemChange()
                                }
                            })
                    }
            ) {
                Text(
                    text = "-",
                    color = colorResource(R.color.Brown),
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}