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
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.download.DownloadFragment
import ru.be_more.orange_forum.ui.favorire.FavoriteFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment


class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter(presenterId = "presID", tag = "presTag")
    lateinit var mainPresenter: MainPresenter

    private var timestamp: Long = 0
    private var disposable: Disposable? = null

    override fun setActionBarTitle(title: String? ){
        supportActionBar?.title = title
    }

    override fun showCategoryFragment() {
        val fragment = CategoryFragment.getCategoryFragment { boardId, title ->
                mainPresenter.setBoardTitle(title)
                mainPresenter.setBoard(boardId)
            }

        when {
            supportFragmentManager.findFragmentByTag(CAT_TAG) != null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(CAT_TAG)!!)
                .commit()
            supportFragmentManager.findFragmentById(R.id.container) == null -> supportFragmentManager
                .beginTransaction()
                .add(R.id.container, fragment, CAT_TAG)
                .show(fragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, fragment, CAT_TAG)
                .show(fragment)
                .commit()
        }

        mainPresenter.setCurrentFragmentTag(CAT_TAG)

        setActionBarTitle()
    }

    override fun showBoardFragment(isNew: Boolean) {
        removeThreadMarks()

        val fragment =
            BoardFragment.getBoardFragment({ threadNum, title ->
                mainPresenter.setThreadTitle(title)
                mainPresenter.setThread(threadNum)
            }, mainPresenter.getBoardId())

        when{
            supportFragmentManager.findFragmentByTag(BOARD_TAG) == null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, fragment, BOARD_TAG)
                .show(fragment)
                .commit()
            isNew -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .remove(supportFragmentManager.findFragmentByTag(BOARD_TAG)!!)
                .add(R.id.container, fragment, BOARD_TAG)
                .show(fragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(BOARD_TAG)!!)
                .commit()
        }

        mainPresenter.setCurrentFragmentTag(BOARD_TAG)

        setActionBarTitle(mainPresenter.getBoardTitle())

        App.getBus().onNext(Pair(BoardEntered, BOARD_TAG))

        bottomNavigationView!!.menu.getItem(1).isChecked = true
    }

    override fun showThreadFragment(isNew: Boolean) {

        val fragment = ThreadFragment.getThreadFragment(mainPresenter.getBoardId(), mainPresenter.getThreadNum())

        when {
            supportFragmentManager.findFragmentByTag(THREAD_TAG) == null -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .add(R.id.container, fragment, THREAD_TAG)
                .show(fragment)
                .commit()
            isNew -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .remove(supportFragmentManager.findFragmentByTag(THREAD_TAG)!!)
                .add(R.id.container, fragment, THREAD_TAG)
                .show(fragment)
                .commit()
            else -> supportFragmentManager
                .beginTransaction()
                .hide(supportFragmentManager.findFragmentByTag(mainPresenter.getCurrentFragmentTag())!!)
                .show(supportFragmentManager.findFragmentByTag(THREAD_TAG)!!)
                .commit()
        }

        mainPresenter.setCurrentFragmentTag(THREAD_TAG)

        setActionBarTitle(mainPresenter.getThreadTitle())

        App.getBus().onNext(Pair(ThreadEntered, THREAD_TAG))

        bottomNavigationView!!.menu.getItem(2).isChecked = true
    }

    override fun showFavoriteFragment() {

        val fragment = FavoriteFragment.getFavoriteFragment({
                boardId, threadNum, title ->
            mainPresenter.setBoard(boardId)
            mainPresenter.setThreadTitle(title)
            setActionBarTitle(title)
            mainPresenter.setThread(threadNum)
        },
            {
                    boardId, boardName->
                mainPresenter.setBoardTitle(boardName)
                setActionBarTitle(boardName)
                mainPresenter.setBoard(boardId)
            },
            { boardId, threadNum ->
                mainPresenter.removeThreadFavoriteMark(boardId, threadNum, true)
            })

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
                .add(R.id.container, fragment, FAVORITE_TAG)
                .show(fragment)
                .commit()

        mainPresenter.setCurrentFragmentTag(FAVORITE_TAG)

        setActionBarTitle("Favorites")

        refreshFavorite()

//        App.getBus().onNext(Pair(ThreadEntered, FAVORITE_TAG))
    }

    override fun showDownloadedFragment() {

        val fragment = DownloadFragment.getDownloadFragment({ boardId, threadNum, title ->
            mainPresenter.setBoard(boardId)
            mainPresenter.setThreadTitle(title)
            setActionBarTitle(title)
            mainPresenter.setThread(threadNum)
        },
            {
                    boardId, threadNum -> mainPresenter.deleteThread(boardId, threadNum, true)
            })

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
                .add(R.id.container, fragment, DOWNLOAD_TAG)
                .show(fragment)
                .commit()

        mainPresenter.setCurrentFragmentTag(FAVORITE_TAG)

        setActionBarTitle("Downloaded")

        refreshDownload()
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

        setActionBarTitle("Preferences")

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
                    showCategoryFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_board -> {
                    showBoardFragment(false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_thread -> {
                    showThreadFragment(false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    showFavoriteFragment()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_downloaded -> {
                    showDownloadedFragment()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private val mOnToolbarItemSelectedListener = Toolbar.OnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {

            R.id.navigation_pref -> {
//                mainPresenter.makePrefFragment()
                val size = supportFragmentManager.fragments.size
                Log.d("M_MainActivity","$size fragments:")
                for (fragment in supportFragmentManager.fragments){
                    Log.d("M_MainActivity","$fragment")
                }

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
                           toolbar.menu.findItem(R.id.navigation_favorite).isVisible = false
                       }

                       is UnfavoriteBoardEntered -> {
                           toolbar.menu.findItem(R.id.navigation_favorite).isVisible = true
                           toolbar.menu.findItem(R.id.navigation_favorite_added).isVisible = false
                       }
                   }
               },
               {
                   Log.e("M_MainActivity","bus error = \n $it")
               }
           )


    }

}