package com.kajianid.android.adapter

import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.kajianid.android.R
import com.kajianid.android.data.Mosque
import com.kajianid.android.databinding.ItemMosqueListBinding
import java.util.*
import kotlin.collections.ArrayList

class MosqueListAdapter : RecyclerView.Adapter<MosqueListAdapter.MosqueListViewHolder>() {

    private val mData = ArrayList<Mosque>()

    fun setData(mosque: ArrayList<Mosque>) {
        mData.clear()
        mData.addAll(mosque)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MosqueListViewHolder {
        val view = ItemMosqueListBinding.inflate(LayoutInflater.from(parent.context))
        return MosqueListViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: MosqueListViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    class MosqueListViewHolder(itemView: ItemMosqueListBinding) : RecyclerView.ViewHolder(itemView.root) {

        private val binding: ItemMosqueListBinding = itemView

        fun bind(mosque: Mosque) {
                binding.tvMosqueName.text = mosque.mosqueName
                if (mosque.address.isNullOrBlank()) {
                    val latLng = mosque.latLng!!
                    val latLngArray = latLng.split(",")
                    val latitude = latLngArray[0].toDouble()
                    val longitude = latLngArray[1].toDouble()
                    val location = LatLng(latitude, longitude)
                    val geocoder = Geocoder(itemView.context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                            location.latitude,
                            location.longitude,
                            1)
                    val address = addresses[0].getAddressLine(0)
                    val city = addresses[0].locality
                    val state = addresses[0].adminArea
                    val country = addresses[0].countryName
                    val postalCode = addresses[0].postalCode

                    val fullAddress = "$address, $city, $state, $country $postalCode"
                    binding.tvMosqueAddress.text = fullAddress
                } else binding.tvMosqueAddress.text = mosque.address

                binding.root.setOnClickListener {
                    val alert = AlertDialog.Builder(binding.root.context)
                    alert.setTitle(mosque.mosqueName)
                            .setMessage(binding.root.context.resources.getString(R.string.choose_action_mosque))
                            .setPositiveButton(binding.root.context.resources.getString(R.string.view_on_maps)) { _, _ ->
                                val gmmIntentUri = Uri.parse("geo:${mosque.latLng!!}?q=${mosque.latLng!!}(${mosque.mosqueName})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                mapIntent.resolveActivity(binding.root.context.packageManager)?.let {
                                    (binding.root.context as Activity).startActivity(mapIntent)
                                }
                            }
                            .setNegativeButton(binding.root.context.resources.getString(R.string.cancel)) { _, _ ->
                                /* no-op */
                            }
                    val alertDialog = alert.create()
                    alertDialog.show()
                }

        }
    }
}