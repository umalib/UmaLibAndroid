package cn.umafan.lib.android.ui.reader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.umafan.lib.android.R
import cn.umafan.lib.android.model.DataBaseHandler

class ReaderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        loadArticle()
    }

    fun loadArticle() {

    }
}