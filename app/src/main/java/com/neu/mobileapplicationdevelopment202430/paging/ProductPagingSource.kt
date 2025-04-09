package com.neu.mobileapplicationdevelopment202430.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.network.ProductApiService
import com.neu.mobileapplicationdevelopment202430.storage.ProductStorage
import retrofit2.HttpException
import java.io.IOException

class ProductPagingSource(
    private val apiService: ProductApiService,
    private val productStorage: ProductStorage
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        try {
            // Start with page 1
            val currentPage = params.key ?: 1

            try {
                // First attempt to fetch from API
                val response = apiService.getPagedProducts(currentPage)

                // Update products with background color and page number
                val products = response.products.map { product ->
                    product.copy(bgColor = response.bgColor, page = response.page)
                }

                // Save
                productStorage.saveProductsForPage(currentPage, products)
                Log.d("ProductPagingSource", "Saved ${products.size} products for page $currentPage")

                // next and previous page
                val prevKey = if (currentPage > 1) currentPage - 1 else null
                val nextKey = if (products.isNotEmpty()) currentPage + 1 else null

                return LoadResult.Page(
                    data = products,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            } catch (e: Exception) {
                Log.e("ProductPagingSource", "API call failed: ${e.message}")

                // If API fails, get data from data
                if (productStorage.hasProductsForPage(currentPage)) {
                    val cachedProducts = productStorage.getProductsForPage(currentPage)
                    Log.d("ProductPagingSource", "Retrieved ${cachedProducts.size} cached products for page $currentPage")

                    if (cachedProducts.isNotEmpty()) {
                        return LoadResult.Page(
                            data = cachedProducts,
                            prevKey = if (currentPage > 1) currentPage - 1 else null,
                            nextKey = currentPage + 1
                        )
                    }
                }

                // If no cached, return error
                return when (e) {
                    is IOException -> LoadResult.Error(IOException("Network error or no internet connection", e))
                    is HttpException -> LoadResult.Error(Exception("Server error: ${e.message()}"))
                    else -> LoadResult.Error(Exception("Failed to load products: ${e.message}"))
                }
            }
        } catch (e: Exception) {
            Log.e("ProductPagingSource", "Unexpected error: ${e.message}")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}