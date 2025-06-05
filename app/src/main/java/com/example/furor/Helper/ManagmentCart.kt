package com.example.furor.Helper

import android.content.Context
import android.widget.Toast
import com.example.furor.model.ItemsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManagmentCart(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val uid: String = auth.currentUser?.uid
        ?: throw IllegalStateException("Пользователь не авторизован")

    // Ссылка на узел "cart/{uid}"
    private val cartRef: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference("cart")
        .child(uid)

    // Локальный кэш и карта title → pushKey
    private val cartCache = arrayListOf<ItemsModel>()
    private val keyMap = mutableMapOf<String, String>()

    init {
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cartCache.clear()
                keyMap.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(ItemsModel::class.java) ?: continue
                    cartCache.add(item)
                    child.key?.let { keyMap[item.title] = it }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Не удалось загрузить корзину: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /** Возвращает копию текущей корзины */
    fun getListCart(): ArrayList<ItemsModel> = ArrayList(cartCache)

    /** Добавляет или обновляет элемент */
    fun insertItem(item: ItemsModel) {
        val existingKey = keyMap[item.title]
        if (existingKey != null) {
            cartRef.child(existingKey).setValue(item)
        } else {
            val newKey = cartRef.push().key ?: return
            keyMap[item.title] = newKey
            cartRef.child(newKey).setValue(item)
        }
        Toast.makeText(context, "Добавлено в корзину", Toast.LENGTH_SHORT).show()
    }

    /** Увеличивает количество и сохраняет */
    fun plusItem(listItem: ArrayList<ItemsModel>, position: Int) {
        val item = listItem[position]
        item.numberInCart++
        insertItem(item)
    }

    /** Уменьшает количество или удаляет полностью */
    fun minusItem(listItem: ArrayList<ItemsModel>, position: Int) {
        val item = listItem[position]
        val key = keyMap[item.title]
        if (item.numberInCart > 1) {
            item.numberInCart--
            if (key != null) cartRef.child(key).setValue(item)
        } else {
            if (key != null) {
                cartRef.child(key).removeValue()
                keyMap.remove(item.title)
            }
        }
    }

    /** Считает общую сумму */
    fun getTotalFee(): Double = cartCache.sumOf { it.price * it.numberInCart }

    /** Оформляет заказ и очищает корзину */
    fun placeOrder() {
        if (cartCache.isEmpty()) {
            Toast.makeText(context, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }
        val ordersRef = FirebaseDatabase
            .getInstance()
            .getReference("orders")
            .child(uid)

        val orderId = ordersRef.push().key ?: return
        val orderData = mapOf(
            "orderId"   to orderId,
            "timestamp" to System.currentTimeMillis(),
            "items"     to cartCache.toList()
        )
        ordersRef.child(orderId)
            .setValue(orderData)
            .addOnSuccessListener {
                cartCache.clear()
                keyMap.clear()
                cartRef.removeValue()
                Toast.makeText(context, "Заказ оформлен", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Ошибка оформления: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
