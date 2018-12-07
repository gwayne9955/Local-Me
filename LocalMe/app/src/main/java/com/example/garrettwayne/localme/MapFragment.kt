package com.example.garrettwayne.localme

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.garrettwayne.localme.data.EventContent
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        if (activity != null) {
            val mapFragment = this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (EventContent.toResetMap) {

            val loc = EventContent.getUserLocationCoordinates()
            val zoomAmtString = PreferenceManager.getDefaultSharedPreferences(context).getString("map_zoom", "0")!!

            val zoomAmt: Float = when (zoomAmtString) {
                "0" -> 10.75f
                "-1" -> 10f
                "1" -> 12f
                else -> 10.5f
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), zoomAmt))

            mMap.clear()

            for (e in EventContent.events) {
                try {
                    var m = mMap.addMarker(
                        MarkerOptions()
                            .position(
                                LatLng(
                                    e.locationInfo.address.latitude.toDouble(),
                                    e.locationInfo.address.longitude.toDouble()
                                )
                            )
                            .title(e.name.text)
                            .snippet(e.description.text.take(180))
                    )
                    m.tag = e.id
                } catch (ex: Exception) {
                    Log.e(ex.toString(), e.toString())
                }
            }
            googleMap.setOnMarkerClickListener(this)
            EventContent.toResetMap = false
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val b = AlertDialog.Builder(activity)
        b.setTitle(p0?.title)
        b.setMessage(p0?.snippet)
        b.setPositiveButton("Event Details"){ _, _ ->
            val intent = Intent(view?.context, EventDetailActivity::class.java).apply {
                putExtra(EventDetailFragment.ARG_ITEM_ID, p0?.tag.toString())
            }
            startActivity(intent)
        }
        b.setNegativeButton("Back", null)
        b.create().show()

        return true
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(item: Event?)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
