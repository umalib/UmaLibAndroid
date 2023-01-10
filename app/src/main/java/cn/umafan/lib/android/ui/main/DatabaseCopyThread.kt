package cn.umafan.lib.android.ui.main

import android.content.Context
import android.os.Handler
import android.util.Log
import cn.umafan.lib.android.model.MyApplication
import cn.umafan.lib.android.model.db.DaoMaster
import cn.umafan.lib.android.model.db.DaoSession
import cn.umafan.lib.android.util.ZipUtil
import com.liangguo.androidkit.app.ToastUtil
import org.greenrobot.greendao.database.Database
import java.io.*


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
        try {
            val versionFile = context.getDatabasePath("version")
            var copy = true
            if (versionFile.exists()) {
                val br = BufferedReader(FileReader(versionFile))
                val version = br.readLine()
                br.close()
                Log.i(this.javaClass.simpleName, "db version: $version")
                if (version >= MyApplication.getVersion().name) {
                    copy = false
                }
            }

            if (copy) {
                if (name !== null) {
                    val outputFile: File = context.getDatabasePath("main.db")
                    if (!outputFile.exists()) {
                        outputFile.createNewFile()
                    }
                    val output: String = outputFile.parent!! + "/"
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
                } else{
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
                }
                val bw = BufferedWriter(FileWriter(versionFile))
                bw.write(MyApplication.getVersion().name)
                bw.close()
            }
            Log.i(this.javaClass.simpleName, "copy database done!")
        } catch (e: Exception) {
            e.printStackTrace()
            if (context.getDatabasePath("main.db").exists()) {
                context.getDatabasePath("main.db").delete()
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