package com.example.garrettwayne.localme

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import com.example.garrettwayne.localme.data.EventContent
import kotlinx.android.synthetic.main.activity_filter_event_search.*

class FilterEventSearchActivity : AppCompatActivity() {

    private var search = EventContent.currentEventSearch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_event_search)

        setViewToCurrentEventSearch()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setViewToCurrentEventSearch() {
        range_spinner.setSelection(getSpinnerIndex(range_spinner, EventContent.currentEventSearch.range.toString()))
        keyword_text.setText(EventContent.currentEventSearch.keyword)
        date_spinner.setSelection(getSpinnerIndex(date_spinner, EventContent.currentEventSearch.dateKeyword))
        price_spinner.setSelection(getSpinnerIndex(price_spinner, EventContent.currentEventSearch.price))
        category_spinner.setSelection(getSpinnerIndex(category_spinner, EventContent.currentEventSearch.category))
        sortby_spinner.setSelection(getSpinnerIndex(sortby_spinner, EventContent.currentEventSearch.sortBy))
        filter_adult_switch.isChecked = EventContent.currentEventSearch.allowAdultEvents
    }

    private fun getSpinnerIndex(spinner: Spinner, toCompare: String): Int {
        for (i in 0..spinner.count) {
            if (spinner.adapter.getItem(i).toString() == toCompare)
                return i
        }
        return 0
    }

    fun filterCancelOnClick(view: View) {
        this.finish()
    }

    fun filterApplyOnClick(view: View) {
        var toApply = false

        val range = range_spinner.selectedItem.toString().toInt()
        val keyword = keyword_text.text.toString()
        val date = date_spinner.selectedItem.toString()
        val price = price_spinner.selectedItem.toString()
        val category = category_spinner.selectedItem.toString()
        val sortBy = sortby_spinner.selectedItem.toString()
        val adultEventsOn = filter_adult_switch.isChecked

        if (range != search.range)
            toApply = true
        else if (keyword != search.keyword)
            toApply = true
        else if (date != search.dateKeyword)
            toApply = true
        else if (price != search.price)
            toApply = true
        else if (category != search.category)
            toApply = true
        else if (sortBy != search.sortBy)
            toApply = true
        else if (adultEventsOn != search.allowAdultEvents)
            toApply = true

        if (toApply) {
            val loc = EventContent.getUserLocationCoordinates()
            search = EventSearch(
                loc.latitude,
                loc.longitude,
                range,
                keyword,
                date,
                price,
                category,
                sortBy,
                adultEventsOn
            )
            EventContent.currentEventSearch = search
            setResult(119312)
        }
        this.finish()
    }

    fun filterResetOnClick(view: View) {
        range_spinner.setSelection(getSpinnerIndex(range_spinner, "25"))
        keyword_text.setText("")
        date_spinner.setSelection(getSpinnerIndex(date_spinner, "Today"))
        price_spinner.setSelection(getSpinnerIndex(price_spinner, "Any"))
        category_spinner.setSelection(getSpinnerIndex(category_spinner, "Any"))
        sortby_spinner.setSelection(getSpinnerIndex(sortby_spinner, "Distance"))
        filter_adult_switch.isChecked = false
    }
}
