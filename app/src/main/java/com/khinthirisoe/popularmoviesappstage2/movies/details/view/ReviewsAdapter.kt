package com.khinthirisoe.popularmoviesappstage2.movies.details.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.khinthirisoe.popularmoviesappstage2.R
import com.khinthirisoe.popularmoviesappstage2.movies.details.model.ReviewsResult
import kotlinx.android.synthetic.main.list_reviews.view.*

class ReviewsAdapter(private val mContext: Context, private var mListData: MutableList<ReviewsResult>) :
    RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>() {

    private var animationUp: Animation? = null
    private var animationDown: Animation? = null

    private val Context.layoutInflater get() = LayoutInflater.from(this)

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var content = itemView.txt_content
        var author = itemView.txt_author
        var layout = itemView.layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = mContext.layoutInflater
            .inflate(R.layout.list_reviews, parent, false)

        animationUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up)
        animationDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_down)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.author.text = mListData[position].author
        holder.author.setOnClickListener {
            if (holder.content.isShown) {
                holder.author.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_down_arrow, //right
                    0
                )
                holder.content.visibility = View.GONE
                holder.content.startAnimation(animationUp)
            } else {
                holder.author.setCompoundDrawablesWithIntrinsicBounds(
                    0, //left
                    0, //top
                    R.drawable.ic_up_arrow, //right
                    0
                )
                holder.content.visibility = View.VISIBLE
                holder.content.text = mListData[position].content
                holder.content.startAnimation(animationDown)
            }
        }
    }

    override fun getItemCount(): Int {
        return mListData.size
    }
}
