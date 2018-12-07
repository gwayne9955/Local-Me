package com.example.garrettwayne.localme

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.garrettwayne.localme.EventListFragment.OnListFragmentInteractionListener
import com.example.garrettwayne.localme.data.EventContent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_event.view.*

class MyEventRecyclerViewAdapter(
    private val mValues: List<Event>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Event
            val intent = Intent(v.context, EventDetailActivity::class.java).apply {
                putExtra(EventDetailFragment.ARG_ITEM_ID, item.id)
            }
            v.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_event, parent, false)
        return ViewHolder(view)
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val str = "Start: ${EventContent.getEventDateString(item.start.time)}, ${EventContent.getEventTimeString(item.start.time)}"
        holder.mEventName.text = item.name.text
        holder.mDescription.text = item.description.text.take(150)
        holder.mStart.text = str
        if (item.logo != null && item.logo.url != "")
            Picasso.get().load(item.logo.url).into(holder.mImage)
        else
            Picasso.get().load("https://i.postimg.cc/7ZjRHQyw/sky.jpg").into(holder.mImage)

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mEventName: TextView = mView.event_name
        val mDescription: TextView = mView.event_description
        val mStart: TextView = mView.event_start
        val mImage: ImageView = mView.event_list_image_view

        override fun toString(): String {
            return super.toString() + " '" + mEventName.text + "'"
        }
    }
}
