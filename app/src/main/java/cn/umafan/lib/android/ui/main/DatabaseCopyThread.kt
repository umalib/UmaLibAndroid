package cn.umafan.lib.android.ui.main

import android.content.Context
import android.os.Handler
import cn.umafan.lib.android.R
import cn.umafan.lib.android.beans.DaoMaster
import cn.umafan.lib.android.beans.DaoSession
import cn.umafan.lib.android.model.MyApplication
import org.greenrobot.greendao.database.Database
import java.io.*

class DatabaseCopyThread : Thread() {
    val context = MyApplication.context
    private lateinit var handler: Handler

    companion object {
        private var daoSession: DaoSession? = null
        private val queue = mutableListOf<Handler>()
        private val lock = Object()

        fun addHandler(_handler: Handler) {
            queue.add(_handler)
            synchronized(lock) {
                lock.notify()
            }
        }
    }

    override fun run() {
        while (true) {
            while (queue.size > 0) {
                handler = queue.first()
                queue.removeFirst()
                if (null == daoSession) {
                    copyDatabase()
                    val helper = LibOpenHelper(context, "main.db")
                    val db: Database = helper.readableDb
                    daoSession = DaoMaster(db).newSession()
                }
                val message = handler.obtainMessage()
                message.what = MyApplication.DATABASE_LOADED
                message.obj = daoSession
                handler.sendMessage(message)
            }
            synchronized(lock) {
                lock.wait()
            }
        }
    }

    private fun copyDatabase() {
        val versionFile = context.getDatabasePath("version")
        var copy = true
        if (versionFile.exists()) {
            val br = BufferedReader(FileReader(versionFile))
            val version = br.readLine()
            br.close()
            println("db version: $version")
            if (version >= MyApplication.getVersion().name) {
                copy = false
            }
        }

        if (copy) {
            val myInput: InputStream = context.assets.open("db/main.db")
            val outFile: File = context.getDatabasePath("main.db")

            outFile.parentFile?.mkdirs()
            val myOutput: OutputStream = FileOutputStream(outFile)
            val buffer = ByteArray(5)
            var length: Int = myInput.read(buffer)
            val total = myInput.available()
            var count = 0

            while (length > 0) {
                if (count % 12000 == 0) {
                    val progress = count * length / total.toDouble() * 100
                    val message = handler.obtainMessage()
                    message.what = MyApplication.DATABASE_LOADING
                    message.obj = progress
                    handler.sendMessage(message)
                }
                myOutput.write(buffer, 0, length)
                length = myInput.read(buffer)
                count++
            }
            myOutput.flush()
            myOutput.close()
            myInput.close()

            val bw = BufferedWriter(FileWriter(versionFile))
            bw.write(MyApplication.getVersion().name)
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