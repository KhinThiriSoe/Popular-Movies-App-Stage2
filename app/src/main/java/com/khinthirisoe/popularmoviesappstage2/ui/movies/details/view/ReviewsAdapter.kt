package com.khinthirisoe.popularmoviesappstage2.ui.movies.details.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.ui.base.BaseViewHolder
import com.khinthirisoe.popularmoviesappstage2.ui.movies.details.model.ReviewsResult
import kotlinx.android.synthetic.main.list_reviews.view.*

class ReviewsAdapter(private val mReviewData: MutableList<ReviewsResult>?) :
    RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>() {

    private var animationUp: Animation? = null
    private var animationDown: Animation? = null

    private val Context.layoutInflater get() = LayoutInflater.from(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = parent.context.layoutInflater
            .inflate(R.layout.list_reviews, parent, false)

        animationUp = AnimationUtils.loadAnimation(parent.context, R.anim.slide_up)
        animationDown = AnimationUtils.loadAnimation(parent.context, R.anim.slide_down)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return if (mReviewData != null && mReviewData.size > 0) {
            mReviewData.size
        } else {
            0
        }
    }

    inner class MyViewHolder(itemView: View) : BaseViewHolder(itemView) {

        var content = itemView.txt_content
        var author = itemView.txt_author
        var layout = itemView.layout

        override fun onBind(position: Int) {
            super.onBind(position)

            val list = mReviewData!![position]

            author.text = list.author
            author.setOnClickListener {
                if (content.isShown) {
                    author.setCompoundDrawablesWithIntrinsicBounds(
                        0, //left
                        0, //top
                        R.drawable.ic_down_arrow, //right
                        0
                    )
                    content.visibility = View.GONE
                    content.startAnimation(animationUp)
                } else {
                    author.setCompoundDrawablesWithIntrinsicBounds(
                        0, //left
                        0, //top
                        R.drawable.ic_up_arrow, //right
                        0
                    )
                    content.visibility = View.VISIBLE
                    content.text = list.content
                    content.startAnimation(animationDown)
                }
            }
        }
    }
}
