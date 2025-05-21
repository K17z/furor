package com.example.project1762.Helper

import android.content.Context
import android.widget.Toast
import com.example.furor.Helper.TinyDB
import com.example.furor.Model.ItemsModel

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: ItemsModel) {
        var listItem = getListCart()
        val existAlready = listItem.any { it.title == item.title }
        val index = listItem.indexOfFirst { it.title == item.title }

        if (existAlready) {
            listItem[index].numberInCart = item.numberInCart
        } else {
            listItem.add(item)
        }
        tinyDB.putListObject("CartList", listItem)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("CartList") ?: arrayListOf()
    }

    fun minusItem(listItem: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (listItem[position].numberInCart == 1) {
            listItem.removeAt(position)
        } else {
            listItem[position].numberInCart--
        }
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    fun plusItem(listItem: ArrayList<ItemsModel>, position: Int, listener: ChangeNumberItemsListener) {
        listItem[position].numberInCart++
        tinyDB.putListObject("CartList", listItem)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listItem = getListCart()
        var fee = 0.0
        for (item in listItem) {
            fee += item.price * item.numberInCart
        }
        return fee
    }

    fun placeOrder() {
        val currentCart = getListCart()
        if (currentCart.isEmpty()) return

        val orders = tinyDB.getListObject("OrdersList") ?: arrayListOf<ItemsModel>()
        val newOrders = currentCart.map { it.copy() }
        orders.addAll(newOrders)
        tinyDB.putListObject("OrdersList", orders)
        tinyDB.putListObject("CartList", arrayListOf())
    }

    fun getOrders(): ArrayList<ItemsModel> {
        return tinyDB.getListObject("OrdersList") ?: arrayListOf()
    }

}