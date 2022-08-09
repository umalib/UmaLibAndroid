package cn.umafan.lib.android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import cn.umafan.lib.android.R
import cn.umafan.lib.android.databinding.ActivityUpdateLogBinding
import cn.umafan.lib.android.ui.reader.ReaderViewModel

class UpdateLogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateLogBinding
    private var _mViewModel: ReaderViewModel? = null
    private val mViewModel get() = _mViewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mViewModel =
            ViewModelProvider(this)[ReaderViewModel::class.java]

        binding = ActivityUpdateLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.markdownView.loadMarkdownFromUrl("https://umalib.github.io/UmaLibAndroid/update-log.md")
    }
}