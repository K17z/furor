package com.example.furor.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.furor.Model.CategoryModel
import com.example.furor.Model.ItemsModel
import com.example.furor.Model.SliderModel
import com.example.furor.Reprository.MainReprository

class MainViewModel():ViewModel() {
    private val reprository=MainReprository()
    fun loadBanner(): LiveData<MutableList<SliderModel>>{
        return reprository.loadBanner()
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        return reprository.loadCategory()
    }

    fun loadpopular():LiveData<MutableList<ItemsModel>>{
        return reprository.loadPopular()
    }

    fun loadFiltered(id:String):LiveData<MutableList<ItemsModel>>{
        return reprository.loadFilterd(id)
    }
}