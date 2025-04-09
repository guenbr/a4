package com.neu.mobileapplicationdevelopment202430

import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ApiTesting {

    @Test
    fun testSuccessfulApiWithProducts() = runBlocking {
        // Arrange
        val testProducts = listOf(
            Product("1", "Test Product", "food", "2025-01-01", "9.99", null, "url")
        )
        val repository = TestProductRepository(
            resultToReturn = ProductRepository.Result.Success(testProducts)
        )

        // Act
        val results = repository.getProductsFlow().toList()

        // Assert
        assertEquals(2, results.size) // Loading + Success
        assertTrue(results[0] is ProductRepository.Result.Loading)
        assertTrue(results[1] is ProductRepository.Result.Success)

        val successResult = results[1] as ProductRepository.Result.Success
        assertEquals(1, successResult.data.size)
        assertEquals("Test Product", successResult.data[0].name)
    }

    @Test
    fun testSuccessfulApiWithNoProducts() = runBlocking {
        // Arrange
        val repository = TestProductRepository(
            resultToReturn = ProductRepository.Result.Error("No products found")
        )

        // Act
        val results = repository.getProductsFlow().toList()

        // Assert
        assertEquals(2, results.size) // Loading + Error
        assertTrue(results[0] is ProductRepository.Result.Loading)
        assertTrue(results[1] is ProductRepository.Result.Error)

        val errorResult = results[1] as ProductRepository.Result.Error
        assertEquals("No products found", errorResult.message)
    }

    @Test
    fun testUnsuccessfulApiCall() = runBlocking {
        // Arrange
        val repository = TestProductRepository(
            resultToReturn = ProductRepository.Result.Error("Error loading products: Internal Server Error")
        )

        // Act
        val results = repository.getProductsFlow().toList()

        // Assert
        assertEquals(2, results.size) // Loading + Error
        assertTrue(results[0] is ProductRepository.Result.Loading)
        assertTrue(results[1] is ProductRepository.Result.Error)

        val errorResult = results[1] as ProductRepository.Result.Error
        assertTrue(errorResult.message.contains("Error loading products"))
    }
}

class TestProductRepository(
    private val resultToReturn: ProductRepository.Result<List<Product>>
) : ProductRepository(ProductRetrofitTestClass()) {

    // Override the getProductsFlow method to return a controlled result
    override fun getProductsFlow(): Flow<ProductRepository.Result<List<Product>>> = flow {
        emit(ProductRepository.Result.Loading())
        emit(resultToReturn)
    }

    // We need to pass something to the constructor, but we won't use it
    class ProductRetrofitTestClass : com.neu.mobileapplicationdevelopment202430.network.ProductApiService {
        override fun getProducts() = throw UnsupportedOperationException("Not used in testing")
    }
}