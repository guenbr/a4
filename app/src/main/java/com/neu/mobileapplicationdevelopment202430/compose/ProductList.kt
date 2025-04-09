package com.neu.mobileapplicationdevelopment202430.compose

import android.graphics.Color
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.neu.mobileapplicationdevelopment202430.R
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.viewmodel.ProductViewModel

@Composable
fun ProductListScreen(viewModel: ProductViewModel) {
    val products = viewModel.pagedProducts.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 4.dp
        ) {
            Text(
                text = stringResource(R.string.product_header),
                modifier = Modifier
                    .background(color = androidx.compose.ui.graphics.Color(0xFF6200EE))
                    .padding(16.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = androidx.compose.ui.graphics.Color.White
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (products.loadState.refresh) {
                is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is LoadState.Error -> {
                    val error = products.loadState.refresh as LoadState.Error
                    Text(
                        text = error.error.localizedMessage ?: "Error loading products",
                        color = MaterialTheme.colors.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    if (products.itemCount == 0) {
                        Text(
                            text = stringResource(R.string.no_products),
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 18.sp
                        )
                    } else {
                        ProductGrid(products = products)
                    }
                }
            }

            if (products.loadState.append is LoadState.Loading &&
                products.loadState.refresh !is LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ProductGrid(products: LazyPagingItems<Product>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(products.itemCount) { index ->
            products[index]?.let { product ->
                ProductItem(product = product)
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    val backgroundColor = try {
        val colorInt = Color.parseColor(product.bgColor.ifEmpty {
            if (product.category == "food") "#FFD965" else "#E06666"
        })
        androidx.compose.ui.graphics.Color(colorInt)
    } catch (e: Exception) {
        if (product.category == "food")
            androidx.compose.ui.graphics.Color(0xFFFFD965)
        else
            androidx.compose.ui.graphics.Color(0xFFE06666)
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            // image using glide
            val context = LocalContext.current
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                update = { imageView ->
                    Glide.with(context)
                        .load(product.imageUrl)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 1
            )

            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.body2
            )

            if (product.category == "food" && product.expiryDate != "null") {
                Row {
                    Text(
                        text = stringResource(R.string.expiry_date) + " ",
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = product.expiryDate ?: "N/A",
                        style = MaterialTheme.typography.caption
                    )
                }
            } else if (product.category == "equipment" && product.warranty != "null") {
                Row {
                    Text(
                        text = stringResource(R.string.warranty) + " ",
                        style = MaterialTheme.typography.caption
                    )
                    Text(
                        text = "${product.warranty} ${stringResource(R.string.years)}",
                        style = MaterialTheme.typography.caption
                    )
                }
            }

            if (product.page > 0) {
                Text(
                    text = "Page: ${product.page}",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}