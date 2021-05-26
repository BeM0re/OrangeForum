package ru.be_more.orange_forum.presentation.screens.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import io.reactivex.disposables.Disposable
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.databinding.ActivityMainBinding
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.presentation.screens.base.ActivityViewBindingProvider

class MainActivity : AppCompatActivity() {

    val binding: ActivityMainBinding by ActivityViewBindingProvider(ActivityMainBinding::class.java)
    private var disposable: Disposable? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
//        Localization.setLanguage(this)
//        App.getComponent().inject(null)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
            .navController

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        binding.bottomNavigationView.menu.getItem(1).isEnabled = false
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        subscribe()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        App.getBus().onNext(BackPressed)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun refreshFavorite() {
        App.getBus().onNext(RefreshFavorite)
    }

    fun refreshDownload() {
        App.getBus().onNext(RefreshDownload)
    }

    private fun subscribe(){
       disposable = App.getBus().subscribe(
           {
               when (it) {
                   is AppToBeClosed -> {
                       navController.navigateUp()
                   }

                   is BoardToBeOpened -> {
                       binding.bottomNavigationView.menu.getItem(1).isEnabled = true
                   }

                   is BoardToBeClosed -> {
                       binding.bottomNavigationView.menu.getItem(1).isEnabled = false
                   }

                   is ThreadToBeOpened -> {
                       binding.bottomNavigationView.menu.getItem(2).isEnabled = true
                   }

                   is ThreadToBeClosed -> {
                       binding.bottomNavigationView.menu.getItem(2).isEnabled = false
                   }
               }
           },
           { Log.e("M_MainActivity", "bus error = \n $it") }
       )
    }

}