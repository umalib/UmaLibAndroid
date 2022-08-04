package cn.umafan.lib.android.ui.main

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivityMainBinding
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var _mViewModel: MainViewModel? = null
    private val mViewModel get() = _mViewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_collections, R.id.nav_favorites, R.id.nav_thanks
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarMain.toolbarLayout.title = destination.label
        }

        with(binding) {
            // 搜索框输入监听
            appBarMain.searchView.setOnQueryTextListener(object :
                SimpleSearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String): Boolean {
                    mViewModel.searchParams.value?.keyword = newText
                    return false
                }

                override fun onQueryTextCleared(): Boolean {
                    mViewModel.searchParams.value?.keyword = ""
                    return false
                }

                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d("fucka", "onQueryTextSubmit: $query")
                    mViewModel.searchParams.value?.keyword = query
                    search()
                    return false
                }
            })
            appBarMain.refresh.setOnClickListener {
                mViewModel.searchParams.value?.keyword = ""
                search()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val item = menu.findItem(R.id.action_search)
        binding.appBarMain.searchView.setMenuItem(item)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // 搜索过滤项
            R.id.action_search_settings -> {
                MaterialAlertDialogBuilder(
                    this@MainActivity,
                    com.google.android.material.R.style.MaterialAlertDialog_Material3
                )
                    .setTitle(R.string.search_settings)
                    .setMessage("DAJKlad")
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun search() {
        val bundle = Bundle()
        bundle.putSerializable("searchParams", mViewModel.searchParams.value)
        navController.navigate(R.id.nav_home, bundle)
    }
}