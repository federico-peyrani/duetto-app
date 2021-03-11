package me.federicopeyrani.duetto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import me.federicopeyrani.duetto.FirstLaunchActivity.Companion.KEY_REFRESH_TOKEN
import me.federicopeyrani.duetto.FirstLaunchActivity.Companion.SHARED_PREFS_NAME
import me.federicopeyrani.duetto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /** The view binding for this activity. */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if a token has already been obtained, otherwise start the login activity
        val prefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        if (!prefs.contains(KEY_REFRESH_TOKEN)) {
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