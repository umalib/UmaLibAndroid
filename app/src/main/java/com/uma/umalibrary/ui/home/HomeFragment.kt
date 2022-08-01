package com.uma.umalibrary.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.uma.umalibrary.databinding.FragmentHomeBinding
import com.uma.umalibrary.logic.model.SearchBean

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _homeViewModel: HomeViewModel? = null

    private val homeViewModel get() = _homeViewModel!!

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initView()

        with(binding) {
            nextPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! + 1)
            }
            lastPageBtn.setOnClickListener {
                homeViewModel.currentPage.postValue(homeViewModel.currentPage.value!! - 1)
            }
        }

        homeViewModel.currentPage.observe(viewLifecycleOwner) {
            val pageLen = 5
            if (it <= 1) {
                binding.lastPageBtn.isEnabled = false
            } else if (it >= pageLen) {
                binding.nextPageBtn.isEnabled = false
            } else {
                binding.lastPageBtn.isEnabled =  true
                binding.nextPageBtn.isEnabled = true
            }
            binding.pageNumBtn.text = "第 $it 页"
            homeViewModel.loadArticles(it, arguments?.getSerializable("searchParams") as SearchBean?)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun initView() {
        binding.recyclerView.adapter = homeViewModel.articleDataAdapter
        homeViewModel.loadArticles(homeViewModel.currentPage.value, arguments?.getSerializable("searchParams") as SearchBean?)
    }
}