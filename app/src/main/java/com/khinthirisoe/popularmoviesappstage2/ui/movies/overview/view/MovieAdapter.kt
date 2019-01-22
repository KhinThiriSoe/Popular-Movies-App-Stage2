package com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.khinthirisoe.popularmoviesappstage2.GlideApp
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.data.network.ApiEndPoint
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseViewHolder
import com.khinthirisoe.popularmoviesappstage2.ui.movies.overview.model.MovieResult
import kotlinx.android.synthetic.main.list_movie.view.*

class MovieAdapter(
    private val mMovieData: MutableList<MovieResult>?, private val clickListener: movieListRecyclerViewClickListener) :
    RecyclerView.Adapter<MovieAdapter.MyViewHolder>() {

    private val Context.layoutInflater get() = LayoutInflater.from(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = parent.context.layoutInflater
            .inflate(R.layout.list_movie, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (mMovieData != null && mMovieData.size > 0) {
            mMovieData.size
        } else {
            0
        }
    }

    fun addItems(list: ArrayList<MovieResult>) {
        mMovieData!!.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var poster = itemView.img_poster!!

        override fun onBind(position: Int) {
            super.onBind(position)

            val list = mMovieData!![position]

            GlideApp.with(itemView.context)
                .load(ApiEndPoint.POSTER_PATH + list.posterPath)
                .placeholder(R.drawable.ic_movie)
                .into(poster)

            itemView.setOnClickListener {
                clickListener.listItemClick(list)
            }

//            poster.setOnClickListener {
//                itemView.context.startActivity(
//                    Intent(itemView.context, DetailsActivity::class.java)
//                        .putExtra("data", list)
//                )
//            }
        }
    }

    interface movieListRecyclerViewClickListener {
        fun listItemClick(list: MovieResult)
    }
}
