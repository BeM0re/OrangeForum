package ru.be_more.orange_forum.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var timestamp: Long = 0
    private var disposable: Disposable? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
//        Localization.setLanguage(this)
//        App.getComponent().inject(null)

        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        bottomNavigationView.menu.getItem(1).isEnabled = false
        bottomNavigationView.menu.getItem(2).isEnabled = false

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
                      /* if (System.currentTimeMillis() - timestamp < 2000)
                           super.onBackPressed()
                       else {
                           timestamp = System.currentTimeMillis()
                           Toast.makeText(
                               applicationContext,
                               "Нажмите назад еще раз, чтобы закрыть приложение",
                               Toast.LENGTH_SHORT
                           ).show()
                       }*/
                   }

                   is BoardToBeOpened -> {
                       bottomNavigationView.menu.getItem(1).isEnabled = true
                   }

                   is BoardToBeClosed -> {
                       bottomNavigationView.menu.getItem(1).isEnabled = false
                   }

                   is ThreadToBeOpened -> {
                       bottomNavigationView.menu.getItem(2).isEnabled = true
                   }

                   is ThreadToBeClosed -> {
                       bottomNavigationView.menu.getItem(2).isEnabled = false
                   }
               }
           },
           { Log.e("M_MainActivity", "bus error = \n $it") }
       )
    }

}