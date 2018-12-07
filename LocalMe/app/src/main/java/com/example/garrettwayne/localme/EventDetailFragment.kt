package com.example.garrettwayne.localme

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.garrettwayne.localme.data.EventContent
import kotlinx.android.synthetic.main.fragment_event_detail.view.*

class EventDetailFragment : Fragment() {

    private var item: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                item = EventContent.ITEM_MAP[it.getString(ARG_ITEM_ID)!!]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_event_detail, container, false)

        // Show the event content as text in a TextView.
        item?.let {
            val dateString = EventContent.getEventDateString(it.start.time)
            val timeString = EventContent.getEventTimeString(it.start.time) + " - " + EventContent.getEventTimeString(it.end.time)
            var address1String = ""
            val address2String: String
            if (it.locationInfo.address.localized_multi_line_address_display.size > 1) {
                address1String = it.locationInfo.address.localized_multi_line_address_display[0]
                address2String = it.locationInfo.address.localized_multi_line_address_display[1]
            }
            else
                address2String = it.locationInfo.address.localized_multi_line_address_display[0]
            rootView.event_fragment_date_text_view.text = dateString
            rootView.event_fragment_time_text_view.text = timeString
            rootView.event_fragment_address_text_view1.text = address1String
            rootView.event_fragment_address_text_view2.text = address2String
            rootView.event_fragment_description_text_view.text = it.description.text
        }

        return rootView
    }

    companion object {
        const val ARG_ITEM_ID = "item_id"
    }
}
