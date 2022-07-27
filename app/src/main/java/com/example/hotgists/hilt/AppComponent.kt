package com.example.hotgists.hilt

import android.content.Context
import com.example.hotgists.app.BaseActivity
import com.example.hotgists.app.BaseFragment
import com.example.hotgists.app.feature.DetailsFragment
import com.example.hotgists.app.feature.MainActivity
import com.example.hotgists.app.feature.MainFragment
import com.example.hotgists.hilt.modules.DbModule
import com.example.hotgists.hilt.modules.MainModule
import com.example.hotgists.hilt.modules.NetworkModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        MainModule::class,
        DbModule::class,
        NetworkModule::class
    ]
)
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(activity: BaseActivity)
    fun inject(fragment: BaseFragment)
    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
    fun inject(fragment: DetailsFragment)
}
