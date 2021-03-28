package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.adapters.RecentlyPlayedTracksAdapter
import me.federicopeyrani.duetto.databinding.FragmentHistoryBinding
import me.federicopeyrani.duetto.viewmodels.HistoryViewModel

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var binding: FragmentHistoryBinding

    private val viewModel: HistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHistoryBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val adapter = RecentlyPlayedTracksAdapter()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.recentlyPlayed.scrollToPosition(0)
                }
            }
        })
        binding.recentlyPlayed.adapter = adapter

        lifecycleScope.launchWhenCreated {
            viewModel.recentlyPlayed.collectLatest { adapter.submitData(it) }
        }
    }
}