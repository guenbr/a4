package com.neu.mobileapplicationdevelopment202430

import com.neu.mobileapplicationdevelopment202430.model.Product
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Unit tests for Product model validation
 */
class ProductUnitTest {

    private lateinit var foodProduct: Product
    private lateinit var equipmentProduct: Product

    @Before
    fun setup() {
        // Create a sample food product
        foodProduct = Product(
            id = "1.0",
            name = "Apple",
            category = "food",
            expiryDate = "2025-01-09",
            price = "$55.31",
            warranty = null,
            imageUrl = "https://example.com/apple.jpg"
        )

        // Create a sample equipment product
        equipmentProduct = Product(
            id = "2.0",
            name = "Treadmill",
            category = "equipment",
            expiryDate = null,
            price = "$24.11",
            warranty = "3",
            imageUrl = "https://example.com/treadmill.jpg"
        )
    }

    @Test
    fun testFoodProductProperties() {
        assertEquals("food", foodProduct.category)
        assertNotNull("Food should have expiry date", foodProduct.expiryDate)
        assertNull("Food should not have warranty", foodProduct.warranty)
    }

    @Test
    fun testEquipmentProductProperties() {
        assertEquals("equipment", equipmentProduct.category)
        assertNull("Equipment should not have expiry date", equipmentProduct.expiryDate)
        assertNotNull("Equipment should have warranty", equipmentProduct.warranty)
    }

    @Test
    fun testProductPriceFormat() {
        assertTrue("Price should start with $",
            foodProduct.price.startsWith("$"))
        assertTrue("Price should start with $",
            equipmentProduct.price.startsWith("$"))

        val foodPrice = foodProduct.price.substring(1).toDoubleOrNull()
        val equipmentPrice = equipmentProduct.price.substring(1).toDoubleOrNull()

        assertNotNull("Food price should be convertible to number", foodPrice)
        assertNotNull("Equipment price should be convertible to number", equipmentPrice)
    }

    @Test
    fun testProductIdFormat() {
        assertTrue("Food product ID should contain a period",
            foodProduct.id.contains("."))
        assertTrue("Equipment product ID should contain a period",
            equipmentProduct.id.contains("."))
    }

    @Test
    fun testImageUrlValidity() {
        assertTrue("Food image URL should be a valid URL",
            foodProduct.imageUrl.startsWith("http"))
        assertTrue("Equipment image URL should be a valid URL",
            equipmentProduct.imageUrl.startsWith("http"))
    }
}