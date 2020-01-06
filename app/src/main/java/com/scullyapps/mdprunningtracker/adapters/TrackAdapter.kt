package com.scullyapps.mdprunningtracker.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.scullyapps.mdprunningtracker.R
import com.scullyapps.mdprunningtracker.model.Trackpoint
import kotlinx.android.synthetic.main.view_trackpoint.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TrackAdapter (private val dataset : ArrayList<Trackpoint>, private val comments : HashMap<Int, String>) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private val TAG: String = "TrackAdapter"

    var onItemClick: ((pos:Int) -> Unit)? = null
    var onComment:   ((seq : Int, text : String) -> Unit)? = null


    inner class TrackViewHolder(v : ConstraintLayout) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val seq     : TextView = v.view_track_seq
        val coords  : TextView = v.view_track_coords
        val time    : TextView = v.view_track_timestamp
        val comment : EditText = v.view_track_comment
        val commentBtn : ImageView = v.view_track_btncomment

        override fun onClick(v: View?) {
            onItemClick?.invoke(adapterPosition)
        }

        init {
            v.setOnClickListener(this)

            comment.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    println("Comment has been edited")
                    onComment?.invoke(adapterPosition, p0.toString())
                }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            })
        }

        fun setComment(comment : String) {
            this.comment.setText(comment)
            this.comment.setText("blah")
        }

        fun getComment() = this.comment.text.toString()

    }

    fun setComment() {

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val tView = LayoutInflater.from(parent.context).inflate(R.layout.view_trackpoint, parent, false) as ConstraintLayout

        return TrackViewHolder(tView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = dataset[position]

        holder.seq.text = track.seq.toString()
        holder.coords.text = track.latLng.toString()

        val time = Calendar.getInstance()

        time.clear()
        time.timeInMillis = (track.time * 1000L)

        time.timeZone = TimeZone.getDefault()

        val stamp : String = String.format("%02d:%02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(
            Calendar.MINUTE), time.get(Calendar.SECOND))

        holder.time.text = stamp

        holder.commentBtn.setOnClickListener {
            if(holder.comment.visibility == View.VISIBLE) {
                holder.comment.visibility = View.GONE
            } else {
                holder.comment.visibility = View.VISIBLE
            }
        }

        if(comments.containsKey(position)) {
            val cmt = comments[position]

            if(cmt != null || cmt != "null") {
                holder.comment.setText(cmt)
                holder.comment.visibility = View.VISIBLE
            }
        }

    }

    override fun getItemCount(): Int = dataset.size

}