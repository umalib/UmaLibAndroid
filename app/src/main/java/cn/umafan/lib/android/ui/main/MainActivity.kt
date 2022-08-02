package cn.umafan.lib.android.ui.main

import android.content.Context
import android.os.Bundle
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
import cn.umafan.lib.android.beans.DaoMaster
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.databinding.ActivityMainBinding
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import org.greenrobot.greendao.database.Database
import java.io.*


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var _mViewModel: MainViewModel? = null
    private val mViewModel get() = _mViewModel!!
    private var daoSession: DaoSession? = null

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
                    mViewModel.searchParams.value?.keyword = query
                    search()
                    return false
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    fun daoSession(): DaoSession? {
        if (null == daoSession) {
            copyDataBase(this)

            val helper = LibOpenHelper(this, "main.db")
            val db: Database = helper.readableDb
            daoSession = DaoMaster(db).newSession()
        }
        return daoSession
    }

    private fun copyDataBase(context: Context) {
        val versionFile = getDatabasePath("version")
        var copy = true
        if (versionFile.exists()) {
            val br = BufferedReader(FileReader(versionFile))
            val version = br.readLine()
            br.close()
            println("db version: $version")
            if (version.equals(context.getString(R.string.db_version))) {
                copy = false
            }
        }

        if (copy) {
            val myInput: InputStream = assets.open("db/main.db")
            val outFile: File = getDatabasePath("main.db")

            outFile.parentFile?.mkdirs()
            val myOutput: OutputStream = FileOutputStream(outFile)
            val buffer = ByteArray(5)
            var length: Int = myInput.read(buffer)
            while (length > 0) {
                myOutput.write(buffer, 0, length)
                length = myInput.read(buffer)
            }
            myOutput.flush()
            myOutput.close()
            myInput.close()

            val bw = BufferedWriter(FileWriter(versionFile))
            bw.write(context.getString(R.string.db_version))
            bw.close()
        }
        println("copy database done!")
    }

    class LibOpenHelper(val context: Context, val name: String) :
        DaoMaster.OpenHelper(context, name) {

        override fun onCreate(db: Database?) {

            super.onCreate(db)
        }
    }
}