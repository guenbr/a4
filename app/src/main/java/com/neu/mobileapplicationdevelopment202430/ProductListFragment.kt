package com.neu.mobileapplicationdevelopment202430

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.neu.mobileapplicationdevelopment202430.adapter.ProductAdapter
import com.neu.mobileapplicationdevelopment202430.databinding.FragmentProductListBinding
import com.neu.mobileapplicationdevelopment202430.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProductViewModel
    private val adapter = ProductAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadProducts()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        val application = requireActivity().application as AmazingProducts
        val factory = ProductViewModel.Factory(application.repository)
        viewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ProductViewModel.UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.sortCard.visibility = View.GONE
                        binding.emptyView.visibility = View.GONE
                    }
                    is ProductViewModel.UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.products.isNotEmpty()) {
                            adapter.setProducts(state.products)
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.sortCard.visibility = View.GONE
                            binding.emptyView.visibility = View.GONE
                        } else {
                            binding.emptyView.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.sortCard.visibility = View.GONE
                        }
                    }
                    is ProductViewModel.UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.sortCard.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}