package com.example.hotgists.app

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    val baseActivity: BaseActivity? get() = activity as? BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireActivity().applicationContext
                as App).appComponent.inject(this)

    }
}

