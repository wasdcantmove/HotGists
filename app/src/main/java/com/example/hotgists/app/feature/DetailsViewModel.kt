package com.example.hotgists.app.feature

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.hotgists.api.models.GistList
import com.example.hotgists.app.backend.LocalGistRepository
import com.example.hotgists.app.util.EmptyEventLiveData
import com.example.hotgists.app.util.Event
import com.example.hotgists.app.util.trigger
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val localGistRepository: LocalGistRepository,
) : ViewModel() {

    val gist: LiveData<Event<GistList>> get() = _gist
    private val _gist = MutableLiveData<Event<GistList>>()

    val error = EmptyEventLiveData()

    private val compositeDisposable = CompositeDisposable()

    fun getGist(selectedGist: String) {
        localGistRepository.loadSingleGist(selectedGist)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _gist.trigger(it.first())
            },
                {
                    error.trigger()
                }
            ).let(compositeDisposable::add)
    }

    fun favOrUnFav(id: String, sharedPrefs: SharedPreferences?) {
        val currentFavs = sharedPrefs?.getString(MainViewModel.FAV_STRING, "")
        if (currentFavs?.contains(id) == true) {
            sharedPrefs.edit {
                putString(MainViewModel.FAV_STRING, currentFavs.replace(",$id", ""))
            }
        } else {
            sharedPrefs?.edit {
                putString(MainViewModel.FAV_STRING, "$currentFavs,$id")
            }
        }
    }
}

