package com.example.garrettwayne.localme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.garrettwayne.localme.data.EventContent
import kotlinx.android.synthetic.main.activity_main.*
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import android.support.design.widget.NavigationView
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.Menu
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat

private const val PERMISSION_REQUEST = 10
private const val CONNECTION_TIMEOUT_MILLISECONDS = 60000

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EventListFragment.OnListFragmentInteractionListener {

    private var currentFragment: Fragment? = null
    private var asyncCounter = 0
    private var asyncLength = 0
    private var locationHasLoaded = false
    private lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_events -> {
                if (currentFragment !is EventListFragment) {
                    toolbar.title = "Events"
                    var eventListFragment = supportFragmentManager.findFragmentByTag("EventListFragment")
                    if (eventListFragment != null) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.container, eventListFragment)
                        transaction.commit()
                        currentFragment = eventListFragment
                    }
                    return@OnNavigationItemSelectedListener true
                }
            }
            R.id.navigation_map -> {
                if (currentFragment !is MapFragment) {
                    toolbar.title = "Map"
                    var mapFragment = supportFragmentManager.findFragmentByTag("MapFragment")
                    if (mapFragment != null) {
                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.container, mapFragment)
                        transaction.commit()
                    }
                    else {
                        mapFragment = MapFragment.newInstance("lol", "haha")
                        openFragment(mapFragment)
                    }
                    currentFragment = mapFragment
                    return@OnNavigationItemSelectedListener true
                }
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        startLocationProcess()
    }


    private fun startLocationProcess() {
        openLoadingFragment()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
                getLocation()
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
            getLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                    LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationGps = location
                            EventContent.userLocation.latitude = locationGps!!.latitude
                            EventContent.userLocation.longitude = locationGps!!.longitude
                            if (!locationHasLoaded) {
                                locationHasLoaded = true
                                GetCategoriesAsyncTask().execute("https://www.eventbriteapi.com/v3/categories/?token=${resources.getString(R.string.OAUTH_TOKEN)}")
                            }
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }
                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        if (location != null) {
                            locationNetwork = location
                            EventContent.userLocation.latitude = locationNetwork!!.latitude
                            EventContent.userLocation.longitude = locationNetwork!!.longitude
                            if (!locationHasLoaded) {
                                locationHasLoaded = true
                                GetCategoriesAsyncTask().execute("https://www.eventbriteapi.com/v3/categories/?token=${resources.getString(R.string.OAUTH_TOKEN)}")
                            }
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }
                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if(locationGps!= null && locationNetwork!= null){
                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
                    EventContent.userLocation.latitude = locationNetwork!!.latitude
                    EventContent.userLocation.longitude = locationNetwork!!.longitude
                    if (!locationHasLoaded) {
                        locationHasLoaded = true
                        GetCategoriesAsyncTask().execute("https://www.eventbriteapi.com/v3/categories/?token=${resources.getString(R.string.OAUTH_TOKEN)}")
                    }
                }else{
                    EventContent.userLocation.latitude = locationGps!!.latitude
                    EventContent.userLocation.longitude = locationGps!!.longitude
                    if (!locationHasLoaded) {
                        locationHasLoaded = true
                        GetCategoriesAsyncTask().execute("https://www.eventbriteapi.com/v3/categories/?token=${resources.getString(R.string.OAUTH_TOKEN)}")
                    }
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess)
                getLocation()
        }
    }

    inner class GetCategoriesAsyncTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTION_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTION_TIMEOUT_MILLISECONDS

                val inString = urlConnection.inputStream.bufferedReader().readText()

                publishProgress(inString)
            } catch (ex: Exception) {
                println("HttpURLConnection exception: $ex")
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }

            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var eventCategoriesResponse = Gson().fromJson(values[0], EventCategories::class.java)
                for (e in eventCategoriesResponse.categories)
                    EventContent.eventCategories[e.name] = e.id
            } catch (ex: Exception) {
                println("JSON parsing exception: ${ex.printStackTrace()}")
            }
        }

        override fun onPostExecute(result: String?) {
            val location = EventContent.getUserLocationCoordinates() // Should return the location of the user

            // Using the EventBrite API:

            // range: an integer

            // keyword: a string

            // dateKeyword: default = "Today", "Tomorrow", "This Week", "This Weekend",
            // "Next Week", "This Month", "Next Month"

            // price: "Any", "Free", "Paid"

            // category: "Any", "Music", "Business & Professional", "Food & Drink", "Community & Culture", "Performing & Visual Arts",
            // "Film, Media & Entertainment", "Sports & Fitness", "Health & Wellness", "Science & Technology", "Travel & Outdoor", "Charity & Causes",
            // "Religion & Spirituality", "Family & Education", "Seasonal & Holiday", "Government & Politics", "Fashion & Beauty",
            // "Home & Lifestyle", "Auto, Boat & Air", "Hobbies & Special Interest", "Other", "School Activities"

            // sortby: "Date", "Distance", "Best"

            // allowAdultEvents: boolean

            EventContent.currentEventSearch = EventSearch(
                location.latitude,
                location.longitude,
                25,
                "",
                "Today",
                "Any",
                "Any",
                "Distance",
                false)

            performEventSearch(EventContent.currentEventSearch)
        }
    }

    inner class GetEventsAsyncTask : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTION_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTION_TIMEOUT_MILLISECONDS

                val inString = urlConnection.inputStream.bufferedReader().readText()

                publishProgress(inString)
            } catch (ex: Exception) {
                println("HttpURLConnection exception: $ex")
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }

            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var eventDataResponse = Gson().fromJson(values[0], EventResponse::class.java)
                EventContent.events = eventDataResponse.events.toMutableList()
            } catch (ex: Exception) {
                println("JSON parsing exception: ${ex.printStackTrace()}")
            }
        }

        override fun onPostExecute(result: String?) {
            asyncLength = EventContent.events.size
            if (asyncLength == 0) {
                openNoEventsFragment()
            }
            for (e in EventContent.events) {
                val url = "https://www.eventbriteapi.com/v3/venues/${e.venue_id}/?token=${resources.getString(R.string.OAUTH_TOKEN)}"
                GetEventVenueIdAsyncTask(e).execute(url)
                convertUniversalTime(e)
            }
        }
    }

    inner class GetEventVenueIdAsyncTask(e: Event) : AsyncTask<String, String, String>() {

        private val e = e

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null

            try {
                val url = URL(urls[0])

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = CONNECTION_TIMEOUT_MILLISECONDS
                urlConnection.readTimeout = CONNECTION_TIMEOUT_MILLISECONDS

                val inString = urlConnection.inputStream.bufferedReader().readText()

                publishProgress(inString)
            } catch (ex: Exception) {
                println("HttpURLConnection exception: $ex")
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }

            return " "
        }

        override fun onProgressUpdate(vararg values: String?) {
            try {
                var eventVenueResponse = Gson().fromJson(values[0], EventLocation::class.java)
                e.locationInfo = eventVenueResponse!!
            } catch (ex: Exception) {
                println("JSON parsing exception${ex.printStackTrace()}")
            }
        }

        override fun onPostExecute(result: String?) {
            asyncCounter++
            if (asyncCounter == asyncLength) {
                asyncCounter = 0
                EventContent.addEventstoItems()
                var eventListFragment = supportFragmentManager.findFragmentByTag("EventListFragment")
                if (eventListFragment != null) {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.container, eventListFragment)
                    transaction.commitAllowingStateLoss()
                    currentFragment = eventListFragment

                    navigation.selectedItemId = R.id.navigation_events
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                }
                else {
                    val eventsFragment = EventListFragment.newInstance(1)
                    openFragment(eventsFragment)
                    currentFragment = eventsFragment
                    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
                }

                EventContent.toResetScroll = true
                EventContent.toResetMap = true

                var s = "$asyncLength Events Have Been Found Near You"
                if (asyncLength == 1)
                    s = "$asyncLength Event Has Been Found Near You"
                var t = Toast.makeText(this@MainActivity, s, Toast.LENGTH_LONG)
                t.setGravity(Gravity.BOTTOM, 0, 200)
                t.show()
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, fragment.javaClass.simpleName)
        transaction.addToBackStack(fragment.javaClass.simpleName)
        transaction.commit()
    }

    override fun onListFragmentInteraction(item: Event?) {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == 119312) {
            performEventSearch(EventContent.currentEventSearch)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun performEventSearch(search: EventSearch) {
        openLoadingFragment()
        navigation.setOnNavigationItemSelectedListener(null)

        var category = ""
        if (search.category != "Any") category = EventContent.eventCategories[search.category]!!

        var price = search.price
        if (search.price == "Any") price = ""

        val dateKey: String = when (search.dateKeyword) {
            "Today" -> "today"
            "Tomorrow" -> "tomorrow"
            "This Week" -> "this_week"
            "This Weekend" -> "this_weekend"
            "Next Week" -> "next_week"
            "This Month" -> "this_month"
            "Next Month" -> "next_month"
            else -> ""
        }

        val sort: String = when (search.sortBy) {
            "Distance" -> "distance"
            "Date" -> "date"
            "Best" -> "best"
            else -> ""
        }

        var allowAdultResults = ""
        if (search.allowAdultEvents) allowAdultResults = "on"

        val url = "https://www.eventbriteapi.com/v3/events/search/?q=${search.keyword}&sort_by=$sort&location.within=${search.range}mi&location.latitude=${search.latitude}&location.longitude=${search.longitude}&categories=$category&price=$price&start_date.keyword=$dateKey&include_adult_events=$allowAdultResults&token=${resources.getString(R.string.ACCESS_TOKEN)}"

        GetEventsAsyncTask().execute(url)
    }

    private fun openLoadingFragment() {
        var loadingFragment = supportFragmentManager.findFragmentByTag("LoadingFragment")
        if (loadingFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, loadingFragment)
            transaction.commitAllowingStateLoss()
        }
        else {
            loadingFragment = LoadingFragment.newInstance("lol", "haha")
            openFragment(loadingFragment)
        }
        currentFragment = loadingFragment
    }

    private fun openNoEventsFragment() {
        var noEventsFragment = supportFragmentManager.findFragmentByTag("NoEventsFragment")
        if (noEventsFragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, noEventsFragment)
            transaction.commitAllowingStateLoss()
        }
        else {
            noEventsFragment = NoEventsFragment.newInstance("lol", "haha")
            openFragment(noEventsFragment)
        }
        currentFragment = noEventsFragment
    }

    private fun convertUniversalTime(e: Event) {
        // 2018-12-03T18:00:00
        try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            e.start.time = format.parse(e.start.local)
            e.end.time = format.parse(e.end.local)
        }
        catch (e:java.lang.Exception) {
            Log.d("Date and Time Error: ", e.toString())
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_filter_event_search -> {
                val intent = Intent(this, FilterEventSearchActivity::class.java)
                startActivityForResult(intent, 918)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_search_filter -> {
                val intent = Intent(this, FilterEventSearchActivity::class.java)
                startActivityForResult(intent, 918)
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
