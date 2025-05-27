package com.example.furor.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.furor.model.CategoryModel
import com.example.furor.model.ItemsModel
import com.example.furor.model.SliderModel
import com.google.firebase.database.*

class MainReprository {

    companion object {
        private const val TAG = "MainReprository"
    }

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun loadBanner(): LiveData<MutableList<SliderModel>> {
        val listData = MutableLiveData<MutableList<SliderModel>>()
        val ref = firebaseDatabase.getReference("Banner")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for (child in snapshot.children) {
                    child.getValue(SliderModel::class.java)?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadBanner cancelled", error.toException())
                listData.value = mutableListOf()
            }
        })

        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        val ref = firebaseDatabase.getReference("Category")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (child in snapshot.children) {
                    child.getValue(CategoryModel::class.java)?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadCategory cancelled", error.toException())
                listData.value = mutableListOf()
            }
        })

        return listData
    }

    fun loadPopular(): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("showRecommended").equalTo(true)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (child in snapshot.children) {
                    child.getValue(ItemsModel::class.java)?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadPopular cancelled", error.toException())
                listData.value = mutableListOf()
            }
        })

        return listData
    }

    fun loadFilterd(id: String): LiveData<MutableList<ItemsModel>> {
        val listData = MutableLiveData<MutableList<ItemsModel>>()
        val ref = firebaseDatabase.getReference("Items")
        val query: Query = ref.orderByChild("categoryId").equalTo(id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (child in snapshot.children) {
                    child.getValue(ItemsModel::class.java)?.let { lists.add(it) }
                }
                listData.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "loadFilterd($id) cancelled", error.toException())
                listData.value = mutableListOf()
            }
        })

        return listData
    }
}
