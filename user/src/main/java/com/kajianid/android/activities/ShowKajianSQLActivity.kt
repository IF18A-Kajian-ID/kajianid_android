package com.kajianid.android.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.kajianid.android.R
import com.kajianid.android.data.Kajian
import com.kajianid.android.databases.kajian.DatabaseContract
import com.kajianid.android.databases.kajian.DbKajianHelper
import com.kajianid.android.databases.kajian.MappingHelper
import com.kajianid.android.databinding.ActivityShowKajianBinding
import com.kajianid.android.databinding.ContentShowKajianBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ShowKajianSQLActivity:AppCompatActivity() {
    private lateinit var kajian: Kajian
    private var remindered = false
    private lateinit var dbKajianHelper: DbKajianHelper
    private lateinit var binding1: ActivityShowKajianBinding
    private lateinit var binding2: ContentShowKajianBinding

    companion object{
        const val EXTRA_PARCEL_KAJIAN = "extra_parcel_kajian"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_kajian)
        binding1 = ActivityShowKajianBinding.inflate(layoutInflater)
        binding2 = ContentShowKajianBinding.inflate(layoutInflater)

        binding1.progressMessage.visibility = View.GONE
        binding1.errorMessage.visibility = View.GONE

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lihat Kajian"

        val bundle = intent.extras!!
        kajian = bundle.getParcelable(EXTRA_PARCEL_KAJIAN)!!
        binding2.tvKajianTitle.text = kajian.title
        binding2.tvMosqueAddress.text = kajian.mosque
        binding2.tvUstadzName.text = kajian.ustadzName
        val dateAnnounce = resources.getString(R.string.timestamp_announce) + " " + kajian.dateAnnounce
        val dateDue = resources.getString(R.string.timestamp_due) + " " + kajian.date
        binding2.tvTimestampAnnounce.text = dateAnnounce
        binding2.tvTimestampDue.text = dateDue
        binding2.tvDescription.text = kajian.description
        Glide.with(this)
                .load(kajian.imgResource)
                .into(binding2.imgThumbnail)

        if (kajian.place == "Di Tempat") {
            binding2.btnPlay.visibility = View.GONE
            binding2.tvCategory.text = kajian.place.toString()
            binding2.tvMosqueAddress.text = kajian.address
        } else {
            binding2.btnPlay.visibility = View.VISIBLE
            val category = kajian.place.toString().toUpperCase(Locale.ROOT) + " - Courtesy of YouTube"
            binding2.tvCategory.text = category
            binding2.tvMosqueAddress.text = kajian.mosque
            val uri = kajian.youtubelink.toString()
            binding2.btnPlay.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(i)
            }
        }

        dbKajianHelper = DbKajianHelper.getInstance(this)
        dbKajianHelper.open()
        GlobalScope.launch(Dispatchers.Main) {
            val deferredKajian = async(Dispatchers.IO){
                val cursor = kajian.id?.let { dbKajianHelper.queryById(it) }
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val isReminder  = deferredKajian.await()
            if (isReminder.size == 0){
                remindered = false
                binding1.fabreminder.setImageResource(R.drawable.ic_baseline_notifications_none_24)
            }else{
                remindered = true
                binding1.fabreminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
            }
        }

        binding1.fabreminder.setOnClickListener{
            if (remindered){
                val alert = AlertDialog.Builder(this)
                alert.setTitle(resources.getString(R.string.sure))
                alert.setMessage(resources.getString(R.string.remove_reminder_confirm))
                alert.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    kajian.id?.let { it1 -> dbKajianHelper.deleteById(it1) }
                    remindered = false
                    binding1.fabreminder.setImageResource(R.drawable.ic_baseline_notifications_none_24)
                    Toast.makeText(this, "Pengingat Telah di NonAtifkan",Toast.LENGTH_LONG).show()
                    finish()
                }
                alert.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                    /* no-op */
                }
                alert.create().show()
            }else{
                val values = ContentValues()
                values.put(DatabaseContract.KajianColumns.ID, kajian.id)
                values.put(DatabaseContract.KajianColumns.KAJIAN_TITLE, kajian.title)
                values.put(DatabaseContract.KajianColumns.USTADZ_NAME, kajian.ustadzName)
                values.put(DatabaseContract.KajianColumns.MOSQUE_NAME, kajian.mosque)
                values.put(DatabaseContract.KajianColumns.ADDRESS, kajian.address)
                values.put(DatabaseContract.KajianColumns.PLACE, kajian.place)
                values.put(DatabaseContract.KajianColumns.YOUTUBE_LINK, kajian.youtubelink)
                values.put(DatabaseContract.KajianColumns.DESCRIPTION, kajian.description)
                values.put(DatabaseContract.KajianColumns.IMG_RESOURCE, kajian.imgResource)
                values.put(DatabaseContract.KajianColumns.DATE_ANNOUNCE, kajian.dateAnnounce)
                values.put(DatabaseContract.KajianColumns.DATE_DUE, kajian.date)
                dbKajianHelper.insert(values)
                remindered = true
                binding1.fabreminder.setImageResource(R.drawable.ic_baseline_notifications_active_24)
                Toast.makeText(this, "Pengingat Telah di Aktifkan", Toast.LENGTH_LONG).show()
            }
        }

        binding1.pullToRefresh.setOnRefreshListener {
            binding1.pullToRefresh.isRefreshing = false
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> super.onBackPressed()

        }
        return super.onOptionsItemSelected(item)
    }
}