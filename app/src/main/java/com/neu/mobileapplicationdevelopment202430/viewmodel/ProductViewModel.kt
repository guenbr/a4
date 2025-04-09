package com.neu.mobileapplicationdevelopment202430.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var _allProducts = listOf<Product>()

    val pagedProducts: Flow<PagingData<Product>> = repository.getPagedProducts()
        .cachedIn(viewModelScope)

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.getProductsFlow().collect { result ->
                when (result) {
                    is ProductRepository.Result.Loading -> {
                        _uiState.value = UiState.Loading
                    }
                    is ProductRepository.Result.Success -> {
                        _allProducts = result.data
                        _uiState.value = UiState.Success(_allProducts)
                    }
                    is ProductRepository.Result.Error -> {
                        _uiState.value = UiState.Error(result.message)
                    }
                }
            }
        }
    }

    sealed class UiState {
        data object Loading : UiState()
        data class Success(val products: List<Product>) : UiState()
        data class Error(val message: String) : UiState()
    }

    class Factory(private val repository: ProductRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProductViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}