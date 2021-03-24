package me.federicopeyrani.duetto.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import me.federicopeyrani.duetto.R
import me.federicopeyrani.duetto.databinding.FragmentNavigationBinding

@AndroidEntryPoint
class FragmentNavigation : Fragment(R.layout.fragment_navigation) {

    private lateinit var binding: FragmentNavigationBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentNavigationBinding.bind(view)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }
}