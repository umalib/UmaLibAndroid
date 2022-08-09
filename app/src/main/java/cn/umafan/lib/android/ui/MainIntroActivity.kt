package cn.umafan.lib.android.ui

import android.os.Bundle
import cn.umafan.lib.android.R
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide


class MainIntroActivity : IntroActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setFullscreen(true)
        super.onCreate(savedInstanceState)

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.app_description)
                .image(R.drawable.ic_launcher)
                .background(R.color.white)
                .backgroundDark(R.color.black)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.app_database_intro)
                .description(R.string.app_database_description)
                .image(R.drawable.app_database_intro)
                .background(R.color.blue_500)
                .backgroundDark(R.color.blue_500)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.app_view_intro)
                .description(R.string.app_view_description)
                .image(R.drawable.app_view_intro)
                .background(R.color.purple_500)
                .backgroundDark(R.color.purple_500)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.app_tag_intro)
                .description(R.string.app_tag_description)
                .image(R.drawable.app_tag_intro)
                .background(R.color.teal_200)
                .backgroundDark(R.color.teal_200)
                .scrollable(false)
                .build()
        )

        addSlide(
            SimpleSlide.Builder()
                .title(R.string.app_func_intro)
                .description(R.string.app_func_description)
                .image(R.drawable.app_func_intro)
                .background(R.color.lightPrimary)
                .backgroundDark(R.color.lightPrimary)
                .scrollable(false)
                .build()
        )
    }

    override fun onBackPressed() {}
}