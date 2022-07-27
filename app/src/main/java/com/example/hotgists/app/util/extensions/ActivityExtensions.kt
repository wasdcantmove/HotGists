package com.example.hotgists.app.util.extensions

import androidx.appcompat.app.AppCompatActivity
import com.example.hotgists.app.App

fun AppCompatActivity.component() = (this.application as App).appComponent