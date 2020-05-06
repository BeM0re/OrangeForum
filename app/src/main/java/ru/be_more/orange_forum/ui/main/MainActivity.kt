package ru.be_more.orange_forum.ui.main

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.anadeainc.rxbus.BusProvider
import com.anadeainc.rxbus.Subscribe
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_thread.*
import ru.be_more.orange_forum.R
import ru.be_more.orange_forum.bus.AppToBeClosed
import ru.be_more.orange_forum.bus.BackPressed
import ru.be_more.orange_forum.interfaces.OnBackPressed
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment
import ru.be_more.orange_forum.ui.thread.ThreadFragment

class MainActivity : AppCompatActivity() {

    //TODO перенести в нормальное место
    private var selectedBoard: MutableLiveData<String> = MutableLiveData()
    private var selectedThread:  MutableLiveData<Int> = MutableLiveData()
    private var selectedBoardTitle: MutableLiveData<String> = MutableLiveData()
    private var selectedThreadTitle:  MutableLiveData<String> = MutableLiveData()
    private val bus = BusProvider.getInstance()
    private var timestamp: Long = 0

    private fun setBoard(boardId: String){
        selectedBoard.postValue(boardId)
    }

    private fun setThread(threadNum: Int){
        selectedThread.postValue(threadNum)
    }

    private fun setBoardTitle(boardTitle: String) {
        selectedBoardTitle.postValue(boardTitle)
    }

    private fun setThreadTitle(threadTitle: String) {
        selectedThreadTitle.postValue(threadTitle)
    }

    private fun setActionBarTitle(title: String? = "Orange Forum"){
        supportActionBar?.title = title
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme)
//        Localization.setLanguage(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        toolbar.setOnMenuItemClickListener(mOnPreferenceItemSelectedListener)


        selectedBoard.postValue("")
        selectedThread.postValue(0)

        selectedBoard.observe(this, Observer {
            bottomNavigationView.menu.getItem(1).isEnabled = !selectedBoard.value.isNullOrEmpty()
            if(selectedBoard.value!="") {
                bottomNavigationView.selectedItemId=
                    R.id.navigation_board
            }
        })

        selectedThread.observe(this, Observer {
            bottomNavigationView.menu.getItem(2).isEnabled = selectedThread.value!=0
            if(selectedThread.value!=0) {
                bottomNavigationView.selectedItemId=
                    R.id.navigation_thread
            }
        })

        selectedBoardTitle.observe(this, Observer { setActionBarTitle(it) })
        selectedThreadTitle.observe(this, Observer { setActionBarTitle(it) })

        if (savedInstanceState == null)
            bottomNavigationView.selectedItemId=
                R.id.navigation_category

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
                    val fragment =  CategoryFragment.getCategoryFragment {id, title ->
                        setBoard(id)
                        setBoardTitle(title)
                    }
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    setActionBarTitle()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_board -> {
                    //if selectedBoard.value == null then this menu item is disabled
                    val fragment =
                        BoardFragment.getBoardFragment({
                                num, title ->
                                setThread(num)
                                setThreadTitle(title)
                        }, selectedBoard.value!!)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    setActionBarTitle(selectedBoardTitle.value)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_thread -> {
                    //if selectedBoard.value or selectedThread.value == null then this menu item is disabled
                    val fragment = ThreadFragment.getThreadFragment(
                        selectedBoard.value!!,
                        selectedThread.value!!
                    )
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    setActionBarTitle(selectedThreadTitle.value)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_favorites -> {
                    val fragment = TempFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_downloaded -> {
                    val fragment = TempFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private val mOnPreferenceItemSelectedListener = Toolbar.OnMenuItemClickListener { menuItem ->
        when (menuItem.itemId) {

            R.id.navigation_pref -> {
                val fragment = TempFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
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
        fragmentsGoBack()
//        super.onBackPressed()
    }

    private fun fragmentsGoBack() {
        bus.post(BackPressed)
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
}