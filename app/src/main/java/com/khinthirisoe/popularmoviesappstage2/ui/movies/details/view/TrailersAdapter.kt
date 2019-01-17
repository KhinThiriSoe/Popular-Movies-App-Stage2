package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseViewHolder
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult
import com.khinthirisoe.popularmoviesappstage2.ui.youtube.YouTubePlayerActivity
import com.khinthirisoe.popularmoviesappstage2.utils.Config
import kotlinx.android.synthetic.main.list_videos.view.*


class TrailersAdapter(private val mTrailerData: ArrayList<TrailersResult>?) :
    RecyclerView.Adapter<TrailersAdapter.MyViewHolder>() {

    private val Context.layoutInflater get() = LayoutInflater.from(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = parent.context.layoutInflater
            .inflate(R.layout.list_videos, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (mTrailerData != null && mTrailerData.size > 0) {
            mTrailerData.size
        } else {
            0
        }
    }

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onBind(position: Int) {
            super.onBind(position)

            val youTubeThumbnailView = itemView.youtube_thumbnail_view

            val list = mTrailerData!![position]

            youTubeThumbnailView.initialize(
                Config.DEVELOPER_KEY,
                object : YouTubeThumbnailView.OnInitializedListener {
                    override fun onInitializationSuccess(
                        youTubeThumbnailView: YouTubeThumbnailView,
                        youTubeThumbnailLoader: YouTubeThumbnailLoader
                    ) {
                        youTubeThumbnailLoader.setVideo(list.key)
                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(object :
                            YouTubeThumbnailLoader.OnThumbnailLoadedListener {
                            override fun onThumbnailLoaded(youTubeThumbnailView: YouTubeThumbnailView, s: String) {

                            }

                            override fun onThumbnailError(
                                youTubeThumbnailView: YouTubeThumbnailView,
                                errorReason: YouTubeThumbnailLoader.ErrorReason
                            ) {

                            }
                        })
                    }

                    override fun onInitializationFailure(
                        youTubeThumbnailView: YouTubeThumbnailView,
                        youTubeInitializationResult: YouTubeInitializationResult
                    ) {

                    }
                })
            youTubeThumbnailView.setOnClickListener {
                itemView.context.startActivity(
                    Intent(itemView.context, YouTubePlayerActivity::class.java)
                        .putExtra("key", list.key)
                )
            }
        }
    }
}
