package com.example.hotgists.app.feature

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hotgists.R
import com.example.hotgists.api.models.GistList
import com.example.hotgists.app.BaseFragment
import com.example.hotgists.app.util.extensions.observeEvent
import com.example.hotgists.databinding.FragmentDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : BaseFragment() {

    private var binding: FragmentDetailsBinding? = null
    private val detailsViewModel: DetailsViewModel by viewModels()
    private var sharedPref: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContentView()
        sharedPref = activity?.getSharedPreferences(
            MainViewModel.LAST_TIME_PREF, Context.MODE_PRIVATE
        )
        arguments?.let { bundle ->
            DetailsFragmentArgs.fromBundle(bundle)
                .let {
                    detailsViewModel.getGist(it.selectedGist)
                }
        }
    }

    private fun setupContentView() {
        observeEvent(detailsViewModel.gist, ::showGistDetails)
        observeEvent(detailsViewModel.error) { showToast() }
    }

    private fun showGistDetails(gistList: GistList) {
        binding?.itemId?.text = gistList.id
        binding?.itemUrl?.text = gistList.url
        binding?.itemFileName?.text = gistList.files.keys.toString()
        binding?.favIcon?.setOnClickListener {
            gistList.id?.let { detailsViewModel.favOrUnFav(it, sharedPref) }
            setFavView(gistList)
        }
        setFavView(gistList)
    }

    private fun setFavView(gistList: GistList) {
        if (sharedPref?.getString(MainViewModel.FAV_STRING, "")
                ?.contains(gistList.id.toString()) == true
        ) {
            context?.let { glideWith(it, binding?.favIcon, R.drawable.ic_fav_full) }
        } else {
            context?.let { glideWith(it, binding?.favIcon, R.drawable.ic_fav_boarder) }
        }
    }

    private fun glideWith(context: Context, contentImage: ImageView?, image: Int) {
        if (contentImage != null) {
            Glide
                .with(context)
                .load(context.getDrawable(image))
                .fallback(R.drawable.ic_fav_boarder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(contentImage)
        }
    }

    private fun showToast() =
        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()

}