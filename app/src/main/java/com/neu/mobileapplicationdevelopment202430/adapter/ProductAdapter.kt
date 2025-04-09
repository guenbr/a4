package com.neu.mobileapplicationdevelopment202430.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neu.mobileapplicationdevelopment202430.R
import com.neu.mobileapplicationdevelopment202430.model.Product

class ProductAdapter : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products = emptyList<Product>()

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productContainer: ConstraintLayout = itemView.findViewById(R.id.productContainer)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val expiryDateLabel: TextView = itemView.findViewById(R.id.expiryDateLabel)
        val expiryDateValue: TextView = itemView.findViewById(R.id.expiryDateValue)
        val warrantyLabel: TextView = itemView.findViewById(R.id.warrantyLabel)
        val warrantyValue: TextView = itemView.findViewById(R.id.warrantyValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]

        holder.productName.text = product.name
        holder.productPrice.text = product.price

        when (product.category) {
            "food" -> {
                holder.productContainer.setBackgroundColor(Color.parseColor("#FFD965"))
                holder.expiryDateLabel.visibility = View.VISIBLE
                holder.expiryDateValue.visibility = View.VISIBLE
                holder.warrantyLabel.visibility = View.GONE
                holder.warrantyValue.visibility = View.GONE
                holder.expiryDateValue.text = product.expiryDate ?: "N/A"
            }
            "equipment" -> {
                holder.productContainer.setBackgroundColor(Color.parseColor("#E06666"))
                holder.expiryDateLabel.visibility = View.GONE
                holder.expiryDateValue.visibility = View.GONE
                holder.warrantyLabel.visibility = View.VISIBLE
                holder.warrantyValue.visibility = View.VISIBLE
                holder.warrantyValue.text = "${product.warranty ?: "N/A"} " +
                        holder.itemView.context.getString(R.string.years)
            }
        }

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.productImage)
    }

    fun setProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}