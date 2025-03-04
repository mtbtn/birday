package com.minar.birday.adapters

import android.graphics.drawable.AnimatedVectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.minar.birday.fragments.HomeFragment
import com.minar.birday.R
import com.minar.birday.model.EventResult
import com.minar.birday.utilities.OnItemClickListener
import kotlinx.android.synthetic.main.event_row.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class EventAdapter internal constructor(homeFragment: HomeFragment?): ListAdapter<EventResult, EventAdapter.EventViewHolder>(EventsDiffCallback()) {
    private val fragment = homeFragment
    private val activityScope = CoroutineScope(Dispatchers.Main)
    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false))
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Can't use elsewhere without overriding as a public function
    public override fun getItem(position: Int): EventResult {
        return super.getItem(position)
    }

    inner class EventViewHolder (view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        private val favoriteButton: ImageView = view.favoriteButton
        private val eventPerson: TextView = view.eventPerson
        private val eventDate: TextView = view.eventDate

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        // Set every necessary text and click action in each row
        fun bind(event: EventResult?) {
            val personName = event?.name + " " + event?.surname
            val formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
            val originalDate = event?.originalDate?.format(formatter)
            eventPerson.text = personName
            eventDate.text = originalDate

            // Manage the favorite logic
            if(event?.favorite == false) favoriteButton.setImageResource(R.drawable.animated_to_favorite)
            else favoriteButton.setImageResource(R.drawable.animated_from_favorite)
            favoriteButton.setOnClickListener {
                if(event?.favorite == true) {
                    event.favorite = false
                    activityScope.launch {
                        delay(800)
                        favoriteButton.setImageResource(R.drawable.animated_to_favorite)
                        fragment?.updateFavorite(event)
                    }
                    (favoriteButton.drawable as AnimatedVectorDrawable).start()
                }
                else {
                    event!!.favorite = true
                    activityScope.launch {
                        delay(800)
                        favoriteButton.setImageResource(R.drawable.animated_from_favorite)
                        fragment?.updateFavorite(event)
                    }
                    (favoriteButton.drawable as AnimatedVectorDrawable).start()
                }

            }
        }

        override fun onClick(v: View?) {
            itemClickListener?.onItemClick(adapterPosition, v)
        }

        override fun onLongClick(v: View?): Boolean {
            itemClickListener?.onItemLongClick(adapterPosition, v)
            return true
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

}

class EventsDiffCallback : DiffUtil.ItemCallback<EventResult>() {
    override fun areItemsTheSame(oldItem: EventResult, newItem: EventResult): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventResult, newItem: EventResult): Boolean {
        return oldItem == newItem
    }
}