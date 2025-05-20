package com.example.furor.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.content.edit

class CartItemsViewModal: ViewModel() {
    private val itemCounts = mutableStateMapOf<String, Int>()
    private val itemPriceCounts = mutableStateMapOf<String, Int>()

    fun getCount(productId: String): Int {
        return itemCounts[productId] ?: 1 // ← по умолчанию 1
    }

    fun getPrice(productId: String, price: Double) : Int {
        return itemPriceCounts[productId] ?: price.toInt()
    }

    fun plusCount(productId: String, defPrice: Double, context: Context) {
        val current = itemCounts[productId] ?: 1
        itemCounts[productId] = current + 1

        sum(productId,current + 1, defPrice.toInt(), context)
        saveCounts(context)
    }

    fun minusCount(productId: String, defPrice: Double, context: Context) {
        val current = itemCounts[productId] ?: 1
        if (current > 1) {
            itemCounts[productId] = current - 1
            sum(productId,current - 1, defPrice.toInt(), context)
            saveCounts(context)
        }
    }

    fun saveCounts(context: Context) {
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            clear() // очищаем старые значения
            for ((productId, count) in itemCounts) // сохраняем каждую пару productId → count
                putInt("count_$productId", count)
        }
    }

    fun loadCounts(context: Context) {
        val sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        sharedPreferences.all.forEach { (key, value) -> // загружаем все значения, начинающиеся с "count_"
            if (key.startsWith("count_") && value is Int) {
                val productId = key.removePrefix("count_")
                itemCounts[productId] = value
            }
        }
    }

    private fun sum(productId: String, itemCount: Int, defPrice: Int, context: Context){
        val finalSum = itemCount * defPrice
        itemPriceCounts[productId] = finalSum

        savePrice(context)
    }

    fun savePrice(context: Context) {
        val sharedPreferences = context.getSharedPreferences("prefsPrice", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            clear() // очищаем старые значения
            for ((productId, count) in itemPriceCounts) // сохраняем каждую пару productId → price
                putInt("price_$productId", count)
        }
    }

    fun loadPrice(context: Context) {
        val sharedPreferences = context.getSharedPreferences("prefsPrice", Context.MODE_PRIVATE)

        sharedPreferences.all.forEach { (key, value) -> // загружаем все значения, начинающиеся с "price_"
            if (key.startsWith("price_") && value is Int) {
                val productId = key.removePrefix("price_")
                itemPriceCounts[productId] = value
            }
        }
    }

    fun getFinalPrice(productId: String, defPrice: Double): Int {
        val count = itemCounts[productId] ?: 1
        return (count * defPrice).toInt()
    }
}