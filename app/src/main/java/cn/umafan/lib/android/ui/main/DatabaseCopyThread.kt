package cn.umafan.lib.android.ui.main

import android.content.Context
import android.os.Handler
import android.util.Log
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.db.DaoMaster
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.util.ZipUtil
import org.greenrobot.greendao.database.Database
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class DatabaseCopyThread : Thread() {
    val context = MyApplication.context
    private lateinit var handler: Handler

    companion object {
        private var daoSession: DaoSession? = null
        private val queue = mutableListOf<Pair<Handler, String?>>()
        private val lock = Object()

        fun addHandler(_handler: Handler, fileName: String? = null) {
            queue.add(Pair(_handler, fileName))
            synchronized(lock) {
                lock.notify()
            }
        }

        fun clearDb() {
            daoSession = null
        }

        fun reload() {
            MyApplication.context.getDatabasePath("main.db").delete()
        }
    }

    override fun run() {
        while (true) {
            while (queue.size > 0) {
                handler = queue.first().first
                val dbName = queue.first().second
                queue.removeFirst()
                if (null == daoSession) {
                    copyDatabase(dbName)
                    if (context.getDatabasePath("main.db").exists()) {
                        val helper = LibOpenHelper(context, "main.db")
                        val db: Database = helper.readableDb
                        daoSession = DaoMaster(db).newSession()
                    }
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

    private fun copyDatabase(name: String? = null) {
        val dbFile: File = context.getDatabasePath("main.db")
        try {
            dbFile.parentFile?.mkdirs()
            if (null !== name) {
                if (!dbFile.exists()) {
                    dbFile.createNewFile()
                }
                val output: String = dbFile.parent!! + "/"
                val input: String = context.getDatabasePath(name).path
                val unzippedFileName = ZipUtil.unzip(input, output)
                val unzippedFile = context.getDatabasePath(unzippedFileName)
                if (unzippedFile.exists()) {
                    unzippedFile.renameTo(context.getDatabasePath("main.db"))
                    // 删除压缩包
                    context.getDatabasePath(name).delete()
                } else {
                    return
                }
                Log.i(this.javaClass.simpleName, "unzip database done!")
            } else if (!dbFile.exists()) {
                val inputStream: InputStream = context.assets.open("db/main.db")
                val outputStream: OutputStream = FileOutputStream(dbFile)
                val buffer = ByteArray(5)
                var length: Int = inputStream.read(buffer)
                val total = inputStream.available()
                var count = 0

                while (length > 0) {
                    if (count % 12000 == 0) {
                        val progress = count * length / total.toDouble() * 100
                        val message = handler.obtainMessage()
                        message.what = MyApplication.DATABASE_LOADING
                        message.obj = progress
                        handler.sendMessage(message)
                    }
                    outputStream.write(buffer, 0, length)
                    length = inputStream.read(buffer)
                    count++
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                Log.i(this.javaClass.simpleName, "copy database done!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (dbFile.exists()) {
                dbFile.delete()
            }
        }
    }

    class LibOpenHelper(val context: Context, val name: String) :
        DaoMaster.OpenHelper(context, name) {

        override fun onCreate(db: Database?) {
            super.onCreate(db)
        }
    }
}