package ru.be_more.orange_forum.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.be_more.orange_forum.App
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.*
import ru.be_more.orange_forum.consts.*
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var mainPresenter: MainPresenter

    private val bus = BusProvider.getInstance()
    private var timestamp: Long = 0
    private var disposable: Disposable? = null

    override fun setActionBarTitle(title: String? ){
        supportActionBar?.title = title
    }


    //TODO отрефакторить покрасивее
    override fun showCategoryFragment(categoryFragment: CategoryFragment) {
        when {
            supportFragmentManager.findFragmentByTag(CAT_TAG) != null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(CAT_TAG)!!)
                .commit()
            supportFragmentManager.findFragmentById(R.id.container) == null -> supportFragmentManager
                .beginTransaction()
                .add(R.id.container, categoryFragment, CAT_TAG)
                .show(categoryFragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, categoryFragment, CAT_TAG)
                .show(categoryFragment)
                .commit()
        }

        mainPresenter.setCurrentFragmentTag(CAT_TAG)

        setActionBarTitle()
    }

    override fun showBoardFragment(boardFragment: BoardFragment) {
        if (supportFragmentManager.findFragmentByTag(BOARD_TAG) != null)
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(BOARD_TAG)!!)
                .commit()
        else{
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, boardFragment, BOARD_TAG)
                .show(boardFragment)
                .commit()

        }

        setActionBarTitle(mainPresenter.getBoardTitle())

        bottomNavigationView!!.menu.getItem(1).isChecked = true
    }

    override fun showThreadFragment(threadFragment: ThreadFragment) {
        if (supportFragmentManager.findFragmentByTag(THREAD_TAG) != null)
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(THREAD_TAG)!!)
                .commit()
        else
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, threadFragment, THREAD_TAG)
                .show(threadFragment)
                .commit()

        setActionBarTitle(mainPresenter.getThreadTitle())

        App.getBus().onNext(Pair(ThreadEntered, THREAD_TAG))

        bottomNavigationView!!.menu.getItem(2).isChecked = true
    }

    override fun showFavoriteFragment(favoriteFragment: Fragment) {
        if (supportFragmentManager.findFragmentByTag(FAVORITE_TAG) != null)
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(FAVORITE_TAG)!!)
                .commit()
        else
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, favoriteFragment, FAVORITE_TAG)
                .show(favoriteFragment)
                .commit()

        setActionBarTitle("Favorites")
    }

    override fun showDownloadedFragment(downloadedFragment: Fragment) {
        if (supportFragmentManager.findFragmentByTag(DOWNLOAD_TAG) != null)
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(DOWNLOAD_TAG)!!)
                .commit()
        else
            supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, downloadedFragment, DOWNLOAD_TAG)
                .show(downloadedFragment)
                .commit()

        setActionBarTitle("Downloaded")
    }

    override fun showPrefFragment(prefFragment: Fragment) {
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
                .add(R.id.container, prefFragment, PREF_TAG)
                .show(prefFragment)
                .commit()

        setActionBarTitle("Preferences")
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
        subscribeInit()
    }

    override fun onDestroy() {
        bus.unregister(this)
        disposable?.dispose()
        super.onDestroy()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

            if (mainPresenter.getCurrentFragmentTag() == THREAD_TAG)
                removeThreadMarks()

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
                mainPresenter.deleteThread(
                    mainPresenter.getBoardId(),
                    mainPresenter.getThreadNum(),
                    false)
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite -> {
                mainPresenter.markThreadFavorite()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite_added -> {
                mainPresenter.removeFavoriteMark(
                    mainPresenter.getBoardId(),
                    mainPresenter.getThreadNum(),
                    false)
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
//        bus.post(BackPressed)
//        Log.d("M_MainActivity","Back pressed")
        App.getBus().onNext(Pair(BackPressed, mainPresenter.getCurrentFragmentTag()))
    }

    override fun refreshFavorite() {
        bus.post(RefreshFavorite)
    }

    override fun refreshDownload() {
        bus.post(RefreshDownload)
    }

    private fun removeThreadMarks(){
        toolbar.menu.findItem(R.id.navigation_download).isVisible = false
        toolbar.menu.findItem(R.id.navigation_download_done).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
        toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
        toolbar.menu.findItem(R.id.navigation_pref).isVisible = true
    }

    private fun subscribeInit(){
           disposable = App.getBus().subscribe (
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

                       is UndownloadedThreadEntered -> {
                           toolbar.menu.findItem(R.id.navigation_download).isVisible = true
                           toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
                       }

                       is DownloadedThreadEntered -> {
                           toolbar.menu.findItem(R.id.navigation_download_done).isVisible = true
                           toolbar.menu.findItem(R.id.navigation_pref).isVisible = false
                       }

                       is FavoriteThreadEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                       }

                       is UnfavoriteThreadEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
                       }
                   }

               },
               {
                   Log.e("M_MainActivity","bus error = \n $it")
               }
           )


    }

 /*   @Subscribe
    public fun closeApp(event: AppToBeClosed){
        Log.d("M_MainActivity","close")
        if(System.currentTimeMillis() - timestamp < 2000) {
            Log.d("M_MainActivity","1")
            super.onBackPressed()
        }
        else {
            Log.d("M_MainActivity","2")
            timestamp = System.currentTimeMillis()
            Toast.makeText(applicationContext,
                "Нажмите назад еще раз, чтобы закрыть приложение",
                Toast.LENGTH_SHORT
            ).show()
        }
    }*/

/*    @Subscribe
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
    }*/
}