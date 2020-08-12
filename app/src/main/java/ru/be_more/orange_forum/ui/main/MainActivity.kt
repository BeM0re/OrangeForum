package ru.be_more.orange_forum.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
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

    private var timestamp: Long = 0
    private var disposable: Disposable? = null

    override fun setActionBarTitle(title: String? ){
        supportActionBar?.title = title
    }

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

    override fun showBoardFragment(boardFragment: BoardFragment, isNew: Boolean) {
        when{
            supportFragmentManager.findFragmentByTag(BOARD_TAG) == null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, boardFragment, BOARD_TAG)
                .show(boardFragment)
                .commit()
            isNew -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .remove(supportFragmentManager.findFragmentByTag(BOARD_TAG)!!)
                .add(R.id.container, boardFragment, BOARD_TAG)
                .show(boardFragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(BOARD_TAG)!!)
                .commit()
        }

        setActionBarTitle(mainPresenter.getBoardTitle())

        App.getBus().onNext(Pair(BoardEntered, BOARD_TAG))

        bottomNavigationView!!.menu.getItem(1).isChecked = true
    }

    override fun showThreadFragment(threadFragment: ThreadFragment, isNew: Boolean) {
        when {
            supportFragmentManager.findFragmentByTag(THREAD_TAG) == null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, threadFragment, THREAD_TAG)
                .show(threadFragment)
                .commit()
            isNew -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .remove(supportFragmentManager.findFragmentByTag(THREAD_TAG)!!)
                .add(R.id.container, threadFragment, THREAD_TAG)
                .show(threadFragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(THREAD_TAG)!!)
                .commit()
        }

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

        App.getBus().onNext(Pair(ThreadEntered, FAVORITE_TAG))
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

        subscribe()
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->

            //не делать при первом запуске приложения, когда ни одного фрагмента не создано и кнопки == null
            if (mainPresenter.getCurrentFragmentTag() != "")
                removeThreadMarks()

            when (menuItem.itemId) {
                R.id.navigation_category -> {
                    mainPresenter.makeCategoryFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_board -> {
                    mainPresenter.makeBoardFragment(false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_thread -> {
                    mainPresenter.makeThreadFragment(false)
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
//                mainPresenter.makePrefFragment()
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
                mainPresenter.markFavorite()
                return@OnMenuItemClickListener true
            }
            R.id.navigation_favorite_added -> {
                mainPresenter.unmarkFavorite()
                /*mainPresenter.removeThreadFavoriteMark(
                    mainPresenter.getBoardId(),
                    mainPresenter.getThreadNum(),
                    false)*/
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
                           toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
                       }

                       is UnfavoriteThreadEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                       }

                       is FavoriteBoardEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = true
                       }

                       is UnfavoriteBoardEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                       }
                   }

               },
               {
                   Log.e("M_MainActivity","bus error = \n $it")
               }
           )


    }

}