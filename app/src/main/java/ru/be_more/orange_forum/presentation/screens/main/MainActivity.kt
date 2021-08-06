package ru.be_more.orange_forum.presentation.screens.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.databinding.ActivityMainBinding
import ru.be_more.orange_forum.presentation.bus.*
import ru.be_more.orange_forum.presentation.screens.base.ActivityViewBindingProvider
import ru.be_more.orange_forum.presentation.screens.base.BaseFragment
import ru.be_more.orange_forum.worker.CheckFavoriteUpdateWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by ActivityViewBindingProvider(ActivityMainBinding::class.java)
    private lateinit var navController: NavController
    private var timestamp: Long = 0

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
        val refreshWorkRequest =
            PeriodicWorkRequestBuilder<CheckFavoriteUpdateWorker> (15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "refreshWorkRequest",
                ExistingPeriodicWorkPolicy.KEEP,
                refreshWorkRequest
            )
    }

    override fun onBackPressed() {
        if ((supportFragmentManager
                .fragments
                .filterIsInstance<NavHostFragment>()
                .firstOrNull()
                ?.childFragmentManager
                ?.fragments?.get(0) as? BaseFragment<*>)
                ?.onBackPressed() != false
        )
            closeApp()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun closeApp(){
        if(System.currentTimeMillis() - timestamp < 2000)
            super.onBackPressed()
        else {
            timestamp = System.currentTimeMillis()
            Toast.makeText(this, R.string.push_again_to_quit, Toast.LENGTH_SHORT).show()
        }
    }

}