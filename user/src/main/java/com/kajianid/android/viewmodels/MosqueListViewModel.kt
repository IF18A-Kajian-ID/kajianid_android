package com.kajianid.android.viewmodels

import android.content.Context
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kajianid.android.R
import com.kajianid.android.adapter.MosqueListAdapter
import com.kajianid.android.data.Mosque
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MosqueListViewModel : ViewModel() {

    private var listMosques = MutableLiveData<ArrayList<Mosque>>()

    fun setMosque(context: Context, searchQuery: String = ""): String? {
        try {
            Looper.prepare()
        } catch (e: Exception) {
        }
        var ret: String? = null
        val endpoint = "api/mosques/user"
        val url = context.resources.getString(R.string.server) + endpoint
        val listItems = ArrayList<Mosque>()
        val client = SyncHttpClient()

        // Request Parameters
        val params = RequestParams()
        params.put("q", searchQuery)

        client.get(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }!!
                val resultObject = JSONObject(result)
                val list = resultObject.getJSONArray("data")

                for (i in 0 until list.length()) {
                    val mosqueJSONObject = list.getJSONObject(i)
                    val mosque = Mosque()

                    with(mosqueJSONObject) {
                        val mosqueId = this.getInt("id")
                        mosque.id = mosqueId
                        mosque.mosqueName = this.getString("name")
                        mosque.address = this.getString("address")
                        mosque.latLng = this.getString("lat_lng")
                    }
                    listItems.add(mosque)
                    //adapter.notifyDataSetChanged()
                }
                listMosques.postValue(listItems)
                ret = null
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                //Toast.makeText(context, "Failure!\n${error?.message}\n${responseBody?.let { String(it) }}\n${url}", Toast.LENGTH_SHORT).show()
                error?.printStackTrace()
                ret = "${responseBody?.let { String(it) }}"
            }
        })
        return ret
    }

    fun setMosqueAsync(context: Context,
                       adapter: MosqueListAdapter,
                       searchQuery: String = ""): Boolean {
        var ret = false
        val endpoint = "api/mosques/user"
        val url = context.resources.getString(R.string.server) + endpoint
        val listItems = ArrayList<Mosque>()
        val client = AsyncHttpClient()

        // Request Parameters
        val params = RequestParams()
        params.put("q", searchQuery)

        client.get(url, params, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                val result = responseBody?.let { String(it) }!!
                val resultObject = JSONObject(result)
                val list = resultObject.getJSONArray("data")

                for (i in 0 until list.length()) {
                    val mosqueJSONObject = list.getJSONObject(i)
                    val mosque = Mosque()

                    with(mosqueJSONObject) {
                        val mosqueId = this.getInt("id")
                        mosque.id = mosqueId
                        mosque.mosqueName = this.getString("name")
                        mosque.address = this.getString("address")
                        mosque.latLng = this.getString("lat_lng")
                    }
                    listItems.add(mosque)
                    adapter.notifyDataSetChanged()
                }
                listMosques.postValue(listItems)
                ret = true
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                Toast.makeText(context, "Failure!\n${error?.message}\n${responseBody?.let { String(it) }}\n${url}", Toast.LENGTH_SHORT).show()
                error?.printStackTrace()
                ret = false
            }
        })
        return ret
    }

    fun getMosque(): LiveData<ArrayList<Mosque>> {
        return listMosques
    }

}