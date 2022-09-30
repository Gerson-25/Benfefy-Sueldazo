package com.syntepro.appbeneficiosbolivia.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.syntepro.appbeneficiosbolivia.entity.service.Category
import com.syntepro.appbeneficiosbolivia.entity.service.CategoryRequest
import com.syntepro.appbeneficiosbolivia.ui.home.domain.CategoryRepository

class CategoryViewModel: ViewModel() {
    // Variables
    private val repo = CategoryRepository()

    fun fetchCategories(request: CategoryRequest): LiveData<MutableList<Category>?> {
        val mutableData = MutableLiveData<MutableList<Category>>()

        repo.getCategories(request).observeForever { mutableData.value = it }

        return mutableData
    }
}