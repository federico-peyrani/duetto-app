package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.databinding.FragmentHomeBinding

class FragmentHome : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
    }
}