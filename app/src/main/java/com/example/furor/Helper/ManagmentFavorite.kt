package com.example.project1762.Helper

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.furor.model.ItemsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ManagmentFavorite(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("FavoritePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getFavoriteList(): ArrayList<ItemsModel> {
        val json = sharedPreferences.getString("favorites", null)
        return try {
            if (json.isNullOrEmpty()) arrayListOf()
            else {
                val type = object : TypeToken<ArrayList<ItemsModel>>() {}.type
                gson.fromJson(json, type) ?: arrayListOf()
            }
        } catch (e: Exception) {
            Log.e("FavoriteError", "Ошибка парсинга избранного: ${e.message}")
            arrayListOf()
        }
    }

    fun insertFavorite(item: ItemsModel) {
        val list = getFavoriteList()
        if (list.none { it.title == item.title }) {
            list.add(item.copy())  // используем копию
            saveList(list)
        }
    }

    fun removeFavorite(item: ItemsModel) {
        val list = getFavoriteList().filterNot { it.title == item.title }
        saveList(ArrayList(list))
    }

    fun isFavorite(item: ItemsModel): Boolean {
        return getFavoriteList().any { it.title == item.title }
    }

    private fun saveList(list: ArrayList<ItemsModel>) {
        try {
            val json = gson.toJson(list)
            sharedPreferences.edit().putString("favorites", json).apply()
        } catch (e: Exception) {
            Log.e("FavoriteError", "Ошибка сохранения избранного: ${e.message}")
        }
    }
}
