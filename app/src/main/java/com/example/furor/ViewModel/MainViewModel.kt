package com.example.furor.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.furor.model.CategoryModel
import com.example.furor.model.ItemsModel
import com.example.furor.model.SliderModel
import com.example.furor.repository.MainReprository

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
}