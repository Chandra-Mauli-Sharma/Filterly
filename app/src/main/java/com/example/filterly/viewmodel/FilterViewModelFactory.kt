package com.example.filterly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.filterly.repository.ApiRepository

class FilterViewModelFactory(val repository: ApiRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilterViewModel(repository) as T
    }
}