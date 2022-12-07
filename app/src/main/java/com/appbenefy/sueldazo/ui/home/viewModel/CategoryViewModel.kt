package com.appbenefy.sueldazo.ui.home.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appbenefy.sueldazo.entity.service.Category
import com.appbenefy.sueldazo.entity.service.CategoryRequest
import com.appbenefy.sueldazo.ui.home.domain.CategoryRepository

class CategoryViewModel: ViewModel() {
    // Variables
    private val repo = CategoryRepository()

    fun fetchCategories(request: CategoryRequest): LiveData<MutableList<Category>?> {
        val mutableData = MutableLiveData<MutableList<Category>>()

        repo.getCategories(request).observeForever { mutableData.value = it }

        return mutableData
    }
}