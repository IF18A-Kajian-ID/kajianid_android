package com.kajianid.android.activities

import androidx.appcompat.app.AppCompatActivity
import com.kajianid.android.data.Kajian
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kajianid.android.R
import com.kajianid.android.databases.kajian.DatabaseContract
import com.kajianid.android.databases.kajian.DbKajianHelper
import com.kajianid.android.databases.kajian.MappingHelper
import com.kajianid.android.databinding.ActivityShowKajianBinding
import com.kajianid.android.databinding.ContentShowKajianBinding
import com.kajianid.android.viewmodels.ShowKajianViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ShowKajianActivity : AppCompatActivity() {
    private lateinit var showKajianViewModel: ShowKajianViewModel
    private var id : String = ""
    private var remindered = false
    private lateinit var dbKajianHelper: DbKajianHelper
    private lateinit var kajian: Kajian
    private lateinit var binding1: ActivityShowKajianBinding
    private lateinit var binding2: ContentShowKajianBinding

    companion object {
        const val EXTRA_KAJIAN_ID = "extra_kajian_id"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_kajian)
        binding1 = ActivityShowKajianBinding.inflate(layoutInflater)
        binding2 = ContentShowKajianBinding.inflate(layoutInflater)

        binding1.progressMessage.visibility = View.VISIBLE
        binding1.errorMessage.visibility = View.GONE

        val bundle = intent.extras
        id = bundle?.get(EXTRA_KAJIAN_ID).toString()


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lihat Kajian"

        // MVVM Show Kajian
        showKajianViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ShowKajianViewModel::class.java)
        showKajianViewModel.getKajian().observe(this, Observer {
            if (it["status"] == true) {
                binding1.progressMessage.visibility = View.GONE
                binding1.errorMessage.visibility = View.GONE
                val title = it["title"].toString()
                val ustadzName = resources.getString(R.string.by) + " " + it["ustadz_name"].toString()
                val mosqueName = it["mosque_name"].toString()
                val address = it["address"].toString()
                val description = it["description"].toString()
                val dateAnnounce = resources.getString(R.string.timestamp_announce) + " " + it["date_announce"].toString()
                val dateDue = resources.getString(R.string.timestamp_due) + " " + it["date_due"]

                binding2.tvKajianTitle.text = title
                binding2.tvUstadzName.text = ustadzName
                binding2.tvDescription.text = description
                binding2.tvTimestampAnnounce.text = dateAnnounce
                binding2.tvTimestampDue.text = dateDue

                if (it["category"] == "Di Tempat") {
                    binding2.btnPlay.visibility = View.GONE
                    binding2.tvCategory.text = it["category"].toString()
                    binding2.tvMosqueAddress.text = address
                } else {
                    binding2.btnPlay.visibility = View.VISIBLE
                    val category = it["category"].toString().toUpperCase(Locale.ROOT) + " - Courtesy of YouTube"
                    binding2.tvCategory.text = category
                    binding2.tvMosqueAddress.text = mosqueName
                    val uri = it["youtube_link"].toString()
                    binding2.btnPlay.setOnClickListener {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(i)
                    }
                }

                if (it["img_resource"] != null) {
                    Glide.with(this)
                            .load(it["img_resource"].toString())
                            .into(binding2.imgThumbnail)
                } else {
                    binding2.imgThumbnail.visibility = View.GONE
                    binding2.btnPlay.visibility = View.GONE
                }
                kajian = Kajian()
                kajian.id = it["id"].toString()
                kajian.title = it["title"].toString()
                kajian.mosque = it["mosque_name"].toString()
                kajian.place = it["category"].toString()
                kajian.ustadzName = it["ustadz_name"].toString()
                kajian.description = it["description"].toString()
                kajian.address = it["address"].toString()
                kajian.youtubelink = it["youtube_link"].toString()
                kajian.date = it["date_due"].toString()
                kajian.dateAnnounce = it["date_announce"].toString()
                kajian.imgResource = it["img_resource"].toString()
            } else {
                binding1.progressMessage.visibility = View.GONE
                binding1.errorMessage.visibility = View.VISIBLE
                val errorMessage = """
                    Error!
                    [${it["code"]}]: ${it["message"]}
                """.trimIndent()
                binding1.tvErrorMessage.text = errorMessage
            }
        })
        showKajianViewModel.setKajianAsync(this, id)
        dbKajianHelper = DbKajianHelper.getInstance(this)
        dbKajianHelper.open()
        GlobalScope.launch(Dispatchers.Main) {
            val deferredKajian = async(Dispatchers.IO){
                val cursor = dbKajianHelper.queryById(id)
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
                    dbKajianHelper.deleteById(id)
                    remindered = false
                    binding1.fabreminder.setImageResource(R.drawable.ic_baseline_notifications_none_24)
                    Toast.makeText(this, "Pengingat Telah di NonAtifkan",Toast.LENGTH_LONG).show()
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
                Toast.makeText(this, "Pengingat Telah di Aktifkan",Toast.LENGTH_LONG).show()
            }
        }
        // Pull-to-Refresh
        binding1.pullToRefresh.setOnRefreshListener {
            binding1.pullToRefresh.isRefreshing = false
            binding1.svKajian.visibility = View.GONE
            binding1.progressMessage.visibility = View.VISIBLE
            binding1.errorMessage.visibility = View.GONE
            GlobalScope.launch(Dispatchers.Main) {
                val deferredKajian = async(Dispatchers.IO) {
                    showKajianViewModel.setKajian(applicationContext, id)
                }
                val it = deferredKajian.await()
                if (it["status"] == true) {
                    binding1.progressMessage.visibility = View.GONE
                    binding1.errorMessage.visibility = View.GONE
                    val title = it["title"].toString()
                    val ustadzName = resources.getString(R.string.by) + " " + it["ustadz_name"].toString()
                    val mosqueName = it["mosque_name"].toString()
                    val address = it["address"].toString()
                    val description = it["description"].toString()
                    val dateAnnounce = resources.getString(R.string.timestamp_announce) + " " + it["date_announce"].toString()
                    val dateDue = resources.getString(R.string.timestamp_due) + " " + it["date_due"]

                    // write to model class
                    kajian.id = it["id"].toString()
                    kajian.title = it["title"].toString()
                    kajian.mosque = it["mosque_name"].toString()
                    kajian.place = it["category"].toString()
                    kajian.youtubelink = it["youtube_link"].toString()
                    kajian.description = it["description"].toString()
                    kajian.address = it["address"].toString()
                    kajian.imgResource = it["img_resource"].toString()
                    kajian.date = it["date_due_unformatted"].toString()

                    binding2.tvKajianTitle.text = title
                    binding2.tvUstadzName.text = ustadzName
                    binding2.tvDescription.text = description
                    binding2.tvTimestampAnnounce.text = dateAnnounce
                    binding2.tvTimestampDue.text = dateDue

                    if (it["category"] == "Di Tempat") {
                        binding2.btnPlay.visibility = View.GONE
                        binding2.tvCategory.text = it["category"].toString()
                        binding2.tvMosqueAddress.text = address
                    } else {
                        binding2.btnPlay.visibility = View.VISIBLE
                        val category = it["category"].toString().toUpperCase(Locale.ROOT) + " - Courtesy of YouTube"
                        binding2.tvCategory.text = category
                        binding2.tvMosqueAddress.text = mosqueName
                        val uri = it["youtube_link"].toString()
                        binding2.btnPlay.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                            startActivity(i)
                        }
                    }

                    if (it["img_resource"] != null) {
                        Glide.with(applicationContext)
                                .load(it["img_resource"].toString())
                                .into(binding2.imgThumbnail)
                    } else {
                        binding2.imgThumbnail.visibility = View.GONE
                        binding2.btnPlay.visibility = View.GONE
                    }
                    binding1.svKajian.visibility = View.VISIBLE
                } else {
                    binding1.progressMessage.visibility = View.GONE
                    binding1.errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                        Error!
                        [${it["code"]}]: ${it["message"]}
                    """.trimIndent()
                    binding1.tvErrorMessage.text = errorMessage
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> super.onBackPressed()

        }
        return super.onOptionsItemSelected(item)
    }
}