package com.example.hotgists.app.feature

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hotgists.R
import com.example.hotgists.api.models.GistList
import com.example.hotgists.databinding.EndItemBinding
import com.example.hotgists.databinding.GistItemBinding


class GistAdapter(
    val clickListener: (String) -> Unit,
    val gistList: List<GistList>,
    val favOrUnFav: (String) -> Unit,
    val sharedPreferences: SharedPreferences?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class GistViewHolder(val binding: GistItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class EndViewHolder(val binding: EndItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val binding = GistItemBinding.inflate(LayoutInflater.from(parent.context))
                GistItemBinding.inflate(LayoutInflater.from(parent.context))
                GistViewHolder(binding)
            }
            VIEW_TYPE_END -> {
                val binding = EndItemBinding.inflate(LayoutInflater.from(parent.context))
                EndItemBinding.inflate(LayoutInflater.from(parent.context))
                EndViewHolder(binding)
            }
            else -> {
                val binding = GistItemBinding.inflate(LayoutInflater.from(parent.context))
                GistItemBinding.inflate(LayoutInflater.from(parent.context))
                GistViewHolder(binding)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < 5) {
            VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_END
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            getItemViewType(position) == VIEW_TYPE_NORMAL -> {
                bindContent(position, holder)
            }
            getItemViewType(position) == VIEW_TYPE_END -> {
                bindEndContent(holder)
            }
        }
    }

    private fun bindEndContent(
        holder: RecyclerView.ViewHolder
    ) {
        val binding = (holder as EndViewHolder).binding
        binding.itemCount.text = "Total Gist Count: " + gistList.size.toString()
    }

    private fun bindContent(
        position: Int,
        holder: RecyclerView.ViewHolder
    ) {
        val binding = (holder as GistViewHolder).binding
        val context = binding.root.context
        val item = gistList[position]
        if (sharedPreferences?.getString(MainViewModel.FAV_STRING, "")
                ?.contains(item.id.toString()) == true
        ) {
            glideWith(context, binding.favIcon, R.drawable.ic_fav_full)
        } else {
            glideWith(context, binding.favIcon, R.drawable.ic_fav_boarder)
        }
        binding.itemId.text = item.id
        binding.itemUrl.text = item.url
        binding.itemFileName.text = item.files.keys.toString()
        binding.favIcon.setOnClickListener {
            item.id?.let { it1 -> favOrUnFav(it1) }
            if (sharedPreferences?.getString(MainViewModel.FAV_STRING, "")
                    ?.contains(item.id.toString()) == true
            ) {
                glideWith(context, binding.favIcon, R.drawable.ic_fav_full)
            } else {
                glideWith(context, binding.favIcon, R.drawable.ic_fav_boarder)
            }
        }
        binding.cardView.setOnClickListener {
            item.id?.let { clickListener.invoke(it) }
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

    override fun getItemCount(): Int {
        return if (gistList.size > 5) {
            6
        } else {
            gistList.size
        }
    }

    companion object {
        const val VIEW_TYPE_NORMAL = 0
        const val VIEW_TYPE_END = 1
    }
}
