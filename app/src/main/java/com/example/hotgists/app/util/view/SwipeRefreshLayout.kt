package com.example.hotgists.app.util.view

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.hotgists.R

fun SwipeRefreshLayout.setContentColorScheme() {
    setColorSchemeResources(
        R.color.colorPrimaryDark,
        R.color.colorPrimary,
        R.color.colorAccent
    )
}