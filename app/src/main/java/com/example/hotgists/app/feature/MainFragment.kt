package com.example.hotgists.app.feature

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hotgists.api.models.GistList
import com.example.hotgists.app.BaseFragment
import com.example.hotgists.app.feature.MainViewModel.Companion.LAST_TIME_PREF
import com.example.hotgists.app.util.extensions.observeEvent
import com.example.hotgists.app.util.view.setContentColorScheme
import com.example.hotgists.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragment() {

    var binding: FragmentMainBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private var adapter: GistAdapter? = null
    private val navController: NavController? get() = baseActivity?.navController
    var sharedPref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContentView()
        setupRefreshLayout()
        binding?.appCompatButton?.setOnClickListener {
            mainViewModel.callGistUserApi(binding?.userNameEntry?.text.toString())
        }
        sharedPref = activity?.getSharedPreferences(
            LAST_TIME_PREF, Context.MODE_PRIVATE
        )
        mainViewModel.provideSharedPreferences(sharedPref)
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.callGistListApi()
    }

    private fun setupRefreshLayout() {
        binding?.refresh?.apply {
            setContentColorScheme()
            setOnRefreshListener { mainViewModel.clearAndReloadData() }
        }
    }

    private fun setupContentView() {
        observeEvent(mainViewModel.gistList, ::updateData)
        observeEvent(mainViewModel.contentLoadFail) { showToast() }
    }


    private fun setupRecyclerView(gistList: List<GistList>?) {
        if (gistList?.size == 0) {
            emptyListEvent()
        } else {
            adapter = context?.let {
                gistList?.let { it1 ->
                    GistAdapter(
                        ::openSelectedGist,
                        it1,
                        ::favOrUnFav,
                        sharedPref
                    )
                }
            }
        }
        binding?.recycler?.layoutManager = LinearLayoutManager(context)
        binding?.recycler?.adapter = adapter
        binding?.refresh?.isRefreshing = false
    }

    private fun favOrUnFav(id: String) {
        mainViewModel.addOrRemoveFromFav(id)
    }

    private fun emptyListEvent() =
        Toast.makeText(context, "No recent gist found for this user.", Toast.LENGTH_LONG).show()

    private fun openSelectedGist(id: String) {
        MainFragmentDirections.actionHotFragmentToDetailsFragment(id).let {
            navController?.navigate(it)
        }
    }

    private fun updateData(gistList: List<GistList>?) {
        setupRecyclerView(gistList)
    }

    private fun showToast() =
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()

}