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
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.TrailersResult
import com.khinthirisoe.popularmoviesappstage2.utils.Config
import com.khinthirisoe.popularmoviesappstage2.ui.youtube.YouTubePlayerActivity
import kotlinx.android.synthetic.main.list_videos.view.*


class TrailersAdapter(private val mContext: Context, private var mListData: ArrayList<TrailersResult>) :
    RecyclerView.Adapter<TrailersAdapter.MyViewHolder>() {

    private val Context.layoutInflater get() = LayoutInflater.from(this)

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val youTubeThumbnailView = itemView.youtube_thumbnail_view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = mContext.layoutInflater
            .inflate(R.layout.list_videos, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.youTubeThumbnailView.initialize(
            Config.DEVELOPER_KEY,
            object : YouTubeThumbnailView.OnInitializedListener {
                override fun onInitializationSuccess(
                    youTubeThumbnailView: YouTubeThumbnailView,
                    youTubeThumbnailLoader: YouTubeThumbnailLoader
                ) {
                    youTubeThumbnailLoader.setVideo(mListData[position].key)
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
        holder.youTubeThumbnailView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, YouTubePlayerActivity::class.java)
                    .putExtra("key", mListData[position].key)
            )
        }
    }

    override fun getItemCount(): Int {
        return mListData.size
    }
}
