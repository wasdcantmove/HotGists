package com.example.hotgists.app.feature

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hotgists.api.models.GistList
import com.example.hotgists.app.backend.GistUseCase
import com.example.hotgists.app.util.EmptyEventLiveData
import com.example.hotgists.app.util.Event
import com.example.hotgists.app.util.trigger
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: GistUseCase
) : ViewModel() {

    var sharedPrefs: SharedPreferences? = null

    private val compositeDisposable = CompositeDisposable()
    val contentLoadFail = EmptyEventLiveData()

    val gistList: LiveData<Event<List<GistList>?>> get() = _gistList
    private val _gistList = MutableLiveData<Event<List<GistList>?>>()

    fun callGistUserApi(userName: String) {
        useCase.loadGistUser(userName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let { content -> onObtainedItems(content) }
            }, {
                contentLoadFail.trigger()
            })
            .let(compositeDisposable::add)
    }

    fun callGistListApi() {
        useCase.loadGistList(checkForLastApiCallTime())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it?.let { content -> onObtainedItems(content) }
                sharedPrefs?.edit {
                    putLong(LAST_TIME, System.currentTimeMillis())
                }
            }, {
                contentLoadFail.trigger()
            })
            .let(compositeDisposable::add)
    }

    private fun checkForLastApiCallTime(): Boolean {
        return if (sharedPrefs?.getLong(LAST_TIME, 0) == 0L) {
            true
        } else {
            (sharedPrefs?.getLong(LAST_TIME, 0)
                ?: 0) > System.currentTimeMillis() + EIGHT_HOURS
        }
    }

    fun clearAndReloadData() = callGistListApi()

    private fun onObtainedItems(content: List<GistList>) = _gistList.trigger(content)

    fun provideSharedPreferences(sharedPref: SharedPreferences?) {
        sharedPrefs = sharedPref
    }

    fun addOrRemoveFromFav(id: String) {
        val currentFavs = sharedPrefs?.getString(FAV_STRING, "")
        if (currentFavs?.contains(id) == true) {
            sharedPrefs?.edit {
                putString(FAV_STRING, currentFavs.replace(",$id", ""))
            }
        } else {
            sharedPrefs?.edit {
                putString(FAV_STRING, "$currentFavs,$id")
            }
        }
    }

    companion object {
        const val LAST_TIME_PREF = "LAST_TIME_PREF"
        private const val LAST_TIME = "CURRENT_TIME"
        const val FAV_STRING = "FAV_STRING"
        const val EIGHT_HOURS = 28800000
    }
}