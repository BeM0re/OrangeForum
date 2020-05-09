package ru.be_more.orange_forum.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment

class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var mainPresenter: MainPresenter

    private val bus = BusProvider.getInstance()
    private var timestamp: Long = 0

    override fun setActionBarTitle(title: String? ){
        supportActionBar?.title = title
    }

    override fun showCategoryFragment(categoryFragment: CategoryFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, categoryFragment, categoryFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle()
    }

    override fun showBoardFragment(boardFragment: BoardFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, boardFragment, boardFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle(mainPresenter.getBoardTitle())

        bottomNavigationView!!.menu.getItem(1).isChecked = true
    }

    override fun showThreadFragment(threadFragment: ThreadFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, threadFragment, threadFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle(mainPresenter.getThreadTitle())

        bottomNavigationView!!.menu.getItem(2).isChecked = true
    }

    override fun showFavoriteFragment(favoriteFragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, favoriteFragment, favoriteFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle("Favorites")
    }

    override fun showDownloadedFragment(downloadedFragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, downloadedFragment, downloadedFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle("Downloaded")
    }

    override fun showPrefFragment(prefFragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, prefFragment, prefFragment.javaClass.simpleName)
            .commit()
        setActionBarTitle("Downloaded")
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

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
//        Localization.setLanguage(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        toolbar.setOnMenuItemClickListener(mOnToolbarItemSelectedListener)

        bottomNavigationView.selectedItemId = R.id.navigation_category

        bus.register(this)

    }

    override fun onDestroy() {
        bus.unregister(this)
        super.onDestroy()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_category -> {
                    mainPresenter.makeCategoryFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_board -> {
                    mainPresenter.makeBoardFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_thread -> {
                    mainPresenter.makeThreadFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    mainPresenter.makeFavoriteFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_downloaded -> {
                    mainPresenter.makeDownloadedFragment()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private val mOnToolbarItemSelectedListener = Toolbar.OnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {

            R.id.navigation_pref -> {
                mainPresenter.makePrefFragment()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_download -> {
                mainPresenter.downloadThread()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_download_done -> {
                mainPresenter.deleteThread()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite -> {
                mainPresenter.markThreadFavorite()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite_added -> {
                mainPresenter.removeFavoriteMark()
                return@OnMenuItemClickListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }

    override fun onBackPressed() {
        bus.post(BackPressed)
//        super.onBackPressed()
    }

    @Subscribe
    fun closeApp(event: AppToBeClosed){
        if(System.currentTimeMillis() - timestamp < 2000) {
            super.onBackPressed()
        }
        else {
            timestamp = System.currentTimeMillis()
            Toast.makeText(applicationContext,
                "Нажмите назад еще раз, чтобы закрыть приложение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @Subscribe
    fun showDownloadThreadButtons(event: UndownloadedThreadEntered){
        toolbar.menu.findItem(R.id.navigation_download).isVisible = true
        toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
    }

    @Subscribe
    fun showUndownloadThreadButtons(event: DownloadedThreadEntered){
        toolbar.menu.findItem(R.id.navigation_download_done).isVisible = true
        toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
    }

    @Subscribe
    fun showFavoriteThreadButtons(event: FavoriteThreadEntered){
        toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
    }

    @Subscribe
    fun showUnfavoriteThreadButtons(event: UnfavoriteThreadEntered){
        toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
    }

    @Subscribe
    fun hideThreadControlButtons(event: ThreadLeaved){
        toolbar.menu.findItem(R.id.navigation_download).isVisible = false
        toolbar.menu.findItem(R.id.navigation_download_done).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
        toolbar.menu.findItem(R.id.navigation_pref).isVisible = true
    }
}