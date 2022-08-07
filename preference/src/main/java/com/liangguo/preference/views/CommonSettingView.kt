package com.liangguo.preference.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import com.liangguo.preference.R


/**
 * @author ldh
 * 时间: 2022/3/17 14:16
 *
 */
@SuppressLint("CustomViewStyleable")
class CommonSettingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr) {

    val titleTextView by lazy {
        findViewById<MaterialTextView>(android.R.id.title)
    }

    val subtitleTextView by lazy {
        findViewById<BaselineGridTextView>(android.R.id.summary)
    }

    val endTextView by lazy {
        findViewById<MaterialTextView>(R.id.textView_end)
    }

    val iconView by lazy {
        findViewById<AppCompatImageView>(android.R.id.icon)
    }

    val endIconView by lazy {
        findViewById<AppCompatImageView>(R.id.imageView_right_icon)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_common_preference, this, true)
        context.obtainStyledAttributes(attrs, R.styleable.CommonSettingsView, defStyleAttr, 0).apply {
            titleTextView.text = getText(R.styleable.CommonSettingsView_title)
            subtitleTextView.text = getText(R.styleable.CommonSettingsView_subtitle)
            endTextView.text = getText(R.styleable.CommonSettingsView_end_text)
            endIconView.isVisible = getBoolean(R.styleable.CommonSettingsView_showEndIcon, true)
            iconView.setImageDrawable(getDrawable(R.styleable.CommonSettingsView_icon))
        }.recycle()
    }

}