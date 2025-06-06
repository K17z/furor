package com.example.furor.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.furor.model.CategoryModel
import com.example.furor.model.ItemsModel
import com.example.furor.model.SliderModel
import com.example.furor.repository.MainReprository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel() : ViewModel() {
    private val reprository = MainReprository()
    fun loadBanner(): LiveData<MutableList<SliderModel>> {
        return reprository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return reprository.loadCategory()
    }

    fun loadpopular(): LiveData<MutableList<ItemsModel>> {
        return reprository.loadPopular()
    }

    fun loadFiltered(id: String): LiveData<MutableList<ItemsModel>> {
        return reprository.loadFilterd(id)
    }
    fun loadAllItems(): LiveData<List<ItemsModel>> {
        val liveData = MutableLiveData<List<ItemsModel>>()
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<ItemsModel>()
                for (itemSnap in snapshot.children) {
                    val item = itemSnap.getValue(ItemsModel::class.java)
                    if (item != null) result.add(item)
                }
                liveData.value = result
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.value = emptyList()
            }
        })
        return liveData
    }
}