package com.kajianid.android.activities

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.kajianid.android.R
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import com.kajianid.android.databinding.FragmentMosqueLocationMapBinding

class MosqueLocationMapFragment : Fragment() {

    private lateinit var mMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var smf: SupportMapFragment? = null
    private val sharedPreferences = context?.getSharedPreferences("root_preferences", Context.MODE_PRIVATE)
    private lateinit var binding: FragmentMosqueLocationMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMosqueLocationMapBinding.inflate(layoutInflater)

        // Class-to-Object Initializer disimpan disini
        geocoder = Geocoder(requireContext().applicationContext, Locale.getDefault())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mosque_location_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        smf = activity?.supportFragmentManager?.findFragmentById(R.id.mapMosque) as SupportMapFragment?

        //mosqueDetails.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        // FIXME: 22/08/2020 Marker pada Maps tidak muncul sama sekali :(
        GlobalScope.launch (Dispatchers.Main) {
            val resultMap: ArrayList<MutableMap<String, String>> = arrayListOf()
            val api = resources.getString(R.string.server) + "api/mosques/user"
            val client = AsyncHttpClient()
            client.get(api, null, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                    val resultRaw = responseBody?.let { String(it) }!!
                    val response = JSONObject(resultRaw)
                    val getData = response.getJSONArray("data")

                    Log.e("TAG", resultRaw)

                    for (i in 0 until getData.length()) {
                        val mutableMap: MutableMap<String, String> = mutableMapOf()
                        val data = getData.getJSONObject(i)
                        val rawPath = data.getString("lat_lng")
                        val mosqueName = data.getString("name")

                        mutableMap["name"] = mosqueName
                        mutableMap["latLng"] = rawPath
                        resultMap.add(mutableMap)
                    }
                    binding.progressBar.visibility = View.GONE
                    smf?.getMapAsync { googleMap ->
                        mMap = googleMap
                        mMap.mapType = sharedPreferences?.getString("map_type", "1")?.let { Integer.parseInt(it) }!!

                        for (i in 0 until resultMap.size - 1) {
                            val value = resultMap[i]

                            val mosqueName = value["name"]!!
                            val path = value["latLng"]?.split(",")!!
                            val latLng: DoubleArray = doubleArrayOf(
                                    path[0].toDouble(),
                                    path[1].toDouble()
                            )

                            val location = LatLng(latLng[0], latLng[1])
                            mMap.addMarker(MarkerOptions().position(location).title(mosqueName))
                        }
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                    Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            })
        }
    }
}