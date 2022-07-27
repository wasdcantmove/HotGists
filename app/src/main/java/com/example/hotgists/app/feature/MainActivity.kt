package com.example.hotgists.app.feature

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.hotgists.R
import com.example.hotgists.app.BaseActivity
import com.example.hotgists.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val navController: NavController get() = findNavController(R.id.navHostMain)

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()
        .also { if (!it) finish() }

    override fun onBackPressed() {
        if (!onSupportNavigateUp()) super.onBackPressed()
    }
}