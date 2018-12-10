package com.example.garrettwayne.localme

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.garrettwayne.localme.data.EventContent
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.util.*

class EventDetailActivity : AppCompatActivity() {

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            event = EventContent.ITEM_MAP[intent.getStringExtra(EventDetailFragment.ARG_ITEM_ID)]

            event_name_text_view.text = event!!.name.text

            if (event?.logo != null && event!!.logo.url != "")
                Picasso.get().load(event!!.logo.url).into(event_image_view)
            else
                Picasso.get().load("https://i.postimg.cc/7ZjRHQyw/sky.jpg").into(event_image_view)

            val fragment = EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        EventDetailFragment.ARG_ITEM_ID,
                        intent.getStringExtra(EventDetailFragment.ARG_ITEM_ID)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.event_detail_container, fragment)
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_event_detail_actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            R.id.action_share_event -> {
                if (event != null) {
                    shareEvent(event!!)
                }
                return true
            }
            R.id.action_event_open_in_browser -> {
                if (event != null) {
                    openEventInBrowser(event!!)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun shareEvent(event: Event) {
        val eventTitle = event.name.text
        val eventUrl = event.url
        val share = Intent(android.content.Intent.ACTION_SEND)
        share.type = "text/plain"
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, eventTitle)
        share.putExtra(Intent.EXTRA_TEXT, eventUrl)

        startActivity(Intent.createChooser(share, "Share Event!"))
    }

    fun notifyEvent(event: Event) {
        val timeThreshold = PreferenceManager.getDefaultSharedPreferences(this).getString("notification_time", "")!!.toInt()

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notifications_new_message", true)) {
//             NotificationUtils().setNotification(Calendar.getInstance().timeInMillis + 5000, this, event)
            NotificationUtils().setNotification(event.start.time.time - timeThreshold.toLong(), this, event)

            val t = Toast.makeText(
                this,
                "You will be reminded ${EventContent.preEventNotificationTable[timeThreshold]} before this event",
                Toast.LENGTH_LONG)
            t.show()
        }
        else {
            val t = Toast.makeText(
                this,
                "Please turn on Notifications in Settings to be notified of events",
                Toast.LENGTH_LONG)
            t.show()
        }
    }

    fun notifyEventButton_OnClick(view: View) {
        notifyEvent(event!!)
    }

    fun getDirectionsButton_OnClick(view: View) {
        val intent = Intent(
            android.content.Intent.ACTION_VIEW,
            Uri.parse("google.navigation:q=${event!!.locationInfo.address.localized_address_display}")
        )
        startActivity(intent)
    }

    fun openEventInBrowser(event: Event) {
        var url = event.url
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://$url"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
