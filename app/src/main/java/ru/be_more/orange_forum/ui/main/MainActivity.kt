package ru.be_more.orange_forum.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.download.DownloadFragment
import ru.be_more.orange_forum.ui.favorire.FavoriteFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment

class MainActivity : AppCompatActivity(), MainView {

    private val mainPresenter: MainPresenter by inject(parameters = {parametersOf(this)})

    private var timestamp: Long = 0
    private var disposable: Disposable? = null
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
//        Localization.setLanguage(this)
//        App.getComponent().inject(null)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)

        bottomNavigationView.menu.getItem(1).isEnabled = false
        bottomNavigationView.menu.getItem(2).isEnabled = false

        toolbar.setOnMenuItemClickListener(mOnToolbarItemSelectedListener)

        subscribe()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun showPrefFragment() {

        val fragment = TempFragment()

        if (supportFragmentManager.findFragmentByTag(PREF_TAG) != null)
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(PREF_TAG)!!)
                .commit()
        else
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, fragment, PREF_TAG)
                .show(fragment)
                .commit()

//        setActionBarTitle("Preferences")

        mainPresenter.setCurrentFragmentTag(PREF_TAG)
    }

    override fun hideBoardMenuItem(){
        bottomNavigationView.menu.getItem(1).isEnabled = false
    }

    override fun hideThreadMenuItem(){
        bottomNavigationView.menu.getItem(2).isEnabled = false
    }

    override fun showBoardMenuItem(){
        bottomNavigationView.menu.getItem(1).isEnabled = true
    }

    override fun showThreadMenuItem(){
        bottomNavigationView.menu.getItem(2).isEnabled = true
    }

    override fun turnFavoriteIcon(isFavorite: Boolean) {
        toolbar.menu.findItem(R.id.navigation_favorite).isVisible = !isFavorite
        toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = isFavorite
    }

    override fun turnDownloadedIcon(isDownloaded: Boolean) {
        toolbar.menu.findItem(R.id.navigation_download).isVisible = !isDownloaded
        toolbar.menu.findItem(R.id.navigation_download_done).isVisible = isDownloaded
    }

    private val mOnToolbarItemSelectedListener = Toolbar.OnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {

            R.id.navigation_pref -> {
                Log.d(
                    "M_MainActivity",
                    "active = ${supportFragmentManager.findFragmentById(R.id.container)}"
                )
                Log.d("M_MainActivity", "saved tag = ${mainPresenter.getCurrentFragmentTag()}")

                return@OnMenuItemClickListener true
            }
            R.id.navigation_download -> {
//                mainPresenter.downloadThread()
                App.getBus().onNext(Pair(AddToDownload, ""))
                return@OnMenuItemClickListener true
            }
            R.id.navigation_download_done -> {
//                mainPresenter.deleteThread(false)
                App.getBus().onNext(Pair(RemoveFromDownload, ""))
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite -> {
//                mainPresenter.markFavorite()
                App.getBus().onNext(Pair(AddToFavorite, ""))
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite_added -> {
//                mainPresenter.unmarkFavorite()
                App.getBus().onNext(Pair(RemoveFromFavorite, ""))
                return@OnMenuItemClickListener true
            }
        }
        false
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.actionbar, menu)
//        return true
//    }

    override fun onBackPressed() {
        App.getBus().onNext(Pair(BackPressed, mainPresenter.getCurrentFragmentTag()))
    }

    override fun refreshFavorite() {
        App.getBus().onNext(Pair(RefreshFavorite, FAVORITE_TAG))
    }

    override fun refreshDownload() {
        App.getBus().onNext(Pair(RefreshDownload, DOWNLOAD_TAG))
    }

    private fun removeThreadMarks(){
        toolbar.menu.findItem(R.id.navigation_download).isVisible = false
        toolbar.menu.findItem(R.id.navigation_download_done).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
        toolbar.menu.findItem(R.id.navigation_pref).isVisible = true
    }

    private fun subscribe(){

       disposable = App.getBus().subscribe(
           {
               when (it.first) {
                   is AppToBeClosed -> {
                       if (System.currentTimeMillis() - timestamp < 2000)
                           super.onBackPressed()
                       else {
                           timestamp = System.currentTimeMillis()
                           Toast.makeText(
                               applicationContext,
                               "Нажмите назад еще раз, чтобы закрыть приложение",
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                   }

                   /*is UndownloadedThreadEntered -> {
                       toolbar.menu.findItem(R.id.navigation_download).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
                   }

                   is DownloadedThreadEntered -> {
                       toolbar.menu.findItem(R.id.navigation_download_done).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
                   }

                   is FavoriteThreadEntered -> {
                       toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
                   }

                   is UnfavoriteThreadEntered -> {
                       toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
                   }

                   is FavoriteBoardEntered -> {
                       toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
                   }

                   is UnfavoriteBoardEntered -> {
                       toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                       toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
                   }*/

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
           {
               Log.e("M_MainActivity", "bus error = \n $it")
           }
       )


    }

}