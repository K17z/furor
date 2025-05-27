package com.example.project1762.Helper

import android.content.Context
import android.widget.Toast
import com.example.furor.helper.TinyDB
import com.example.furor.model.ItemsModel
import com.example.furor.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ManagmentCart(private val context: Context) {

    private val tinyDB = TinyDB(context)

    /** Возвращает список текущих элементов в корзине */
    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    /** Добавляет или обновляет товар в корзине */
    fun insertItem(item: ItemsModel) {
        val listItem = getListCart()
        val index = listItem.indexOfFirst { it.title == item.title }
        if (index >= 0) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Добавлено в корзину", Toast.LENGTH_SHORT).show()
    }

    /** Уменьшает количество или удаляет товар из корзины */
    fun minusItem(
        listItem: ArrayList<ItemsModel>,
        position: Int,
        listener: ChangeNumberItemsListener
    ) {
        if (listItem[position].numberInCart == 1) {
            listItem.removeAt(position)
        } else {
            listItem[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    /** Увеличивает количество товара в корзине */
    fun plusItem(
        listItem: ArrayList<ItemsModel>,
        position: Int,
        listener: ChangeNumberItemsListener
    ) {
        listItem[position].numberInCart++
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    /** Общая сумма всех товаров в корзине */
    fun getTotalFee(): Double =
        getListCart().sumOf { it.price * it.numberInCart }

    /**
     * Оформление заказа:
     * 1) Пушим все позиции в Firebase под /orders/{uid}
     * 2) Очищаем локальную корзину
     */
    fun placeOrder() {
        val cart = getListCart()
        if (cart.isEmpty()) {
            Toast.makeText(context, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(context, "Сначала войдите в аккаунт", Toast.LENGTH_SHORT).show()
            return
        }

        val ordersRef = FirebaseDatabase
            .getInstance()
            .getReference("orders")
            .child(uid)

        // Генерируем один orderId для всего текущего заказа
        val orderId = ordersRef.push().key ?: return

        // Составляем модель заказа
        val order = OrderModel(
            orderId = orderId,
            timestamp = System.currentTimeMillis(),
            items = cart.toList()
        )

        // Сохраняем всю модель за один вызов
        ordersRef.child(orderId).setValue(order)
            .addOnSuccessListener {
                // очистка корзины
                tinyDB.putListObject("CartList", arrayListOf())
                Toast.makeText(context, "Заказ оформлен", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ошибка оформления: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    /** (Опционально) старый локальный список оформленных заказов */
    fun getOrders(): ArrayList<ItemsModel> =
        tinyDB.getListObject("OrdersList") ?: arrayListOf()
}
