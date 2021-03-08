package me.federicopeyrani.duetto

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import me.federicopeyrani.duetto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SHARED_PREFS_NAME = "login"
        private const val TOKEN_KEY = "token"
    }

    /** The view binding for this activity. */
    private lateinit var binding: ActivityMainBinding

    private fun onItem(item: MenuItem): Unit = when (item.itemId) {
        // R.id.page_home -> Unit
        // R.id.page_favourites -> Unit
        else -> Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if a token has already been obtained, otherwise start the login activity
        val prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        if (!prefs.contains(TOKEN_KEY)) {
            // launch login activity
            val intent = Intent(this, FirstLaunchActivity::class.java)
            startActivity(intent)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get the Intent that started this activity and extract the string
        val authOutcome = intent.getIntExtra(FirstLaunchActivity.AUTH_OUTCOME, -1)
        if (authOutcome == FirstLaunchActivity.AUTH_OUTCOME_SUCCESS) {
            // display success message
            val snackbar = Snackbar.make(binding.root,
                                         getString(R.string.login_success),
                                         Snackbar.LENGTH_LONG)
            snackbar.show()
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }
}