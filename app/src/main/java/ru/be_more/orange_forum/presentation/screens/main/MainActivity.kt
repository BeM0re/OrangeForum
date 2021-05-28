package ru.be_more.orange_forum.presentation.screens.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onBackPressed() {
        EventBus.getDefault().post(BackPressed)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun refreshFavorite() {
        EventBus.getDefault().post(RefreshFavorite)
    }

    fun refreshDownload() {
        EventBus.getDefault().post(RefreshDownload)
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: AppToBeClosed) {
        navController.navigateUp()
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: BoardToBeOpened) {
        binding.bottomNavigationView.menu.getItem(1).isEnabled = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: BoardToBeClosed) {
        binding.bottomNavigationView.menu.getItem(1).isEnabled = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: ThreadToBeOpened) {
        binding.bottomNavigationView.menu.getItem(2).isEnabled = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    fun onMessageEvent(event: ThreadToBeClosed) {
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
    }

}