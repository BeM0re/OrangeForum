package ru.be_more.orange_forum

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import ru.be_more.orange_forum.model.Board
import ru.be_more.orange_forum.model.BoardThread
import ru.be_more.orange_forum.ui.TempFragment
import ru.be_more.orange_forum.ui.board.BoardFragment
import ru.be_more.orange_forum.ui.category.CategoryFragment

class MainActivity : AppCompatActivity() {

    //TODO перенести в нормальное место
    var selectedBoard: MutableLiveData<String> = MutableLiveData()
    var selectedThread:  MutableLiveData<Int> = MutableLiveData()

    private fun setBoard(board: Board){
        selectedBoard.postValue(board.id)
    }

    private fun setThread(thread: BoardThread){
        selectedThread.postValue(thread.num)
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
                bottomNavigationView.selectedItemId=R.id.navigation_board
            }
        })

        selectedThread.observe(this, Observer {
            bottomNavigationView.menu.getItem(2).isEnabled = selectedThread.value!=0
        })

        if (savedInstanceState == null)
            bottomNavigationView.selectedItemId=R.id.navigation_category

    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_category -> {
                    val fragment =  CategoryFragment.getCategoryFragment {setBoard(it)}
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_board -> {
                    //if selectedBoard.value == null then this menu item is disabled
                    val fragment = BoardFragment.getBoardFragment({setThread(it)}, selectedBoard.value!!)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_thread -> {
                    val fragment = TempFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                        .commit()
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
}