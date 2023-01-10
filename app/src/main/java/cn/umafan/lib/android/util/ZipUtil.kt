package cn.umafan.lib.android.util

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


/**
 * @author HY
 */
object ZipUtil {
    private const val BUFFER = 1024

    fun unzip(filePath: String?, zipDir: String): String {
        var name = ""
        try {
            var dest: BufferedOutputStream? = null
            var `is`: BufferedInputStream? = null
            var entry: ZipEntry
            val zipfile = ZipFile(filePath)
            val dir: Enumeration<*> = zipfile.entries()
            while (dir.hasMoreElements()) {
                entry = dir.nextElement() as ZipEntry
                if (entry.isDirectory) {
                    name = entry.name
                    name = name.substring(0, name.length - 1)
                    val fileObject = File(zipDir + name)
                    fileObject.mkdir()
                }
                name = entry.name
            }
            val e: Enumeration<*> = zipfile.entries()
            while (e.hasMoreElements()) {
                entry = e.nextElement() as ZipEntry
                if (entry.isDirectory) {
                    continue
                } else {
                    `is` = BufferedInputStream(zipfile.getInputStream(entry))
                    var count: Int
                    val dataByte = ByteArray(BUFFER)
                    val fos = FileOutputStream(zipDir + entry.name)
                    dest = BufferedOutputStream(fos, BUFFER)
                    while (`is`.read(dataByte, 0, BUFFER).also { count = it } != -1) {
                        dest.write(dataByte, 0, count)
                    }
                    dest.flush()
                    dest.close()
                    `is`.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return name
    }
}