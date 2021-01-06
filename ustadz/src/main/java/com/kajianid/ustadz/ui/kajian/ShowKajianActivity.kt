package com.kajianid.ustadz.ui.kajian

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kajianid.ustadz.R
import com.kajianid.ustadz.data.Kajian
import com.kajianid.ustadz.databinding.ActivityShowKajianBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ShowKajianActivity : AppCompatActivity() {

    private var binding: ActivityShowKajianBinding? = null

    private lateinit var showKajianViewModel: ShowKajianViewModel
    private var kajian: Kajian = Kajian()
    private var id: String = ""

    companion object {
        const val EXTRA_KAJIAN_ID = "extra_kajian_id"

        const val RESULT_DELETE = 121
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowKajianBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.svKajian?.visibility = View.GONE
        binding?.progressMessage?.visibility = View.VISIBLE
        binding?.errorMessage?.visibility = View.GONE

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.view_kajian)

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString(EXTRA_KAJIAN_ID, "").toString()

            showKajianViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                    .get(ShowKajianViewModel::class.java)
            showKajianViewModel.kajian.observe(this, {
                if (it["status"] == true) {
                    binding?.progressMessage?.visibility = View.GONE
                    binding?.errorMessage?.visibility = View.GONE

                    val title = it["title"].toString()
                    val ustadzName = resources.getString(R.string.by) + " " + it["ustadz_name"].toString()
                    val mosqueName = it["mosque_name"].toString()
                    val address = it["address"].toString()
                    val description = it["description"].toString()
                    val dateAnnounce = resources.getString(R.string.timestamp_announce) + " " + it["date_announce"].toString()
                    val dateDue = resources.getString(R.string.timestamp_due) + " " + it["date_due"]

                    kajian.id = it["id"].toString()
                    kajian.title = it["title"].toString()
                    kajian.mosqueId = it["mosque_id"].toString()
                    kajian.mosqueName = it["mosque_name"].toString()
                    kajian.place = it["category"].toString()
                    kajian.ytLink = it["youtube_link"].toString()
                    kajian.description = it["description"].toString()
                    kajian.address = it["address"].toString()
                    kajian.imgResource = it["img_resource"].toString()
                    kajian.date = it["date_due_unformatted"].toString()

                    binding?.contentShowKajian?.tvKajianTitle?.text = title
                    binding?.contentShowKajian?.tvUstadzName?.text = ustadzName
                    binding?.contentShowKajian?.tvDescription?.text = description
                    binding?.contentShowKajian?.tvTimestampAnnounce?.text = dateAnnounce
                    binding?.contentShowKajian?.tvTimestampDue?.text = dateDue

                    if (it["category"] == "Di Tempat") {
                        binding?.contentShowKajian?.btnPlay?.visibility = View.GONE
                        binding?.contentShowKajian?.tvCategory?.text = it["category"].toString()
                        binding?.contentShowKajian?.tvMosqueAddress?.text = address
                    } else {
                        binding?.contentShowKajian?.btnPlay?.visibility = View.VISIBLE
                        val category = it["category"].toString().toUpperCase(Locale.ROOT) + " - Courtesy of YouTube"
                        binding?.contentShowKajian?.tvCategory?.text = category
                        binding?.contentShowKajian?.tvMosqueAddress?.text = mosqueName
                        val uri = it["youtube_link"].toString()
                        binding?.contentShowKajian?.btnPlay?.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                            startActivity(i)
                        }
                    }

                    if (it["img_resource"] != null) {
                        Glide.with(this)
                                .load(it["img_resource"].toString())
                                .into(binding?.contentShowKajian?.imgThumbnail!!)
                    } else {
                        binding?.contentShowKajian?.imgThumbnail?.visibility = View.GONE
                        binding?.contentShowKajian?.btnPlay?.visibility = View.GONE
                    }
                    binding?.svKajian?.visibility = View.VISIBLE
                } else {
                    binding?.progressMessage?.visibility = View.GONE
                    binding?.errorMessage?.visibility = View.VISIBLE
                    val errorMessage = """
                        Error!
                        [${it["code"]}]: ${it["message"]}
                    """.trimIndent()
                    binding?.tvErrorMessage?.text = errorMessage
                }
            })
            showKajianViewModel.setKajianAsync(this, id)

            binding?.pullToRefresh?.setOnRefreshListener {
                refreshData()
            }

            binding?.btnRefresh?.setOnClickListener {
                refreshData()
            }
        }
    }

    private fun refreshData() {
        binding?.pullToRefresh?.isRefreshing = false
        binding?.svKajian?.visibility = View.GONE
        binding?.progressMessage?.visibility = View.VISIBLE
        binding?.errorMessage?.visibility = View.GONE
        GlobalScope.launch(Dispatchers.Main) {
            val deferredKajian = async(Dispatchers.IO) {
                showKajianViewModel.setKajian(applicationContext, id)
            }
            val it = deferredKajian.await()
            if (it["status"] == true) {
                binding?.progressMessage?.visibility = View.GONE
                binding?.errorMessage?.visibility = View.GONE
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
                kajian.mosqueId = it["mosque_id"].toString()
                kajian.mosqueName = it["mosque_name"].toString()
                kajian.place = it["category"].toString()
                kajian.ytLink = it["youtube_link"].toString()
                kajian.description = it["description"].toString()
                kajian.address = it["address"].toString()
                kajian.imgResource = it["img_resource"].toString()
                kajian.date = it["date_due_unformatted"].toString()

                binding?.contentShowKajian?.tvKajianTitle?.text = title
                binding?.contentShowKajian?.tvUstadzName?.text = ustadzName
                binding?.contentShowKajian?.tvDescription?.text = description
                binding?.contentShowKajian?.tvTimestampAnnounce?.text = dateAnnounce
                binding?.contentShowKajian?.tvTimestampDue?.text = dateDue

                if (it["category"] == "Di Tempat") {
                    binding?.contentShowKajian?.btnPlay?.visibility = View.GONE
                    binding?.contentShowKajian?.tvCategory?.text = it["category"].toString()
                    binding?.contentShowKajian?.tvMosqueAddress?.text = address
                } else {
                    binding?.contentShowKajian?.btnPlay?.visibility = View.VISIBLE
                    val category = it["category"].toString().toUpperCase(Locale.ROOT) + " - Courtesy of YouTube"
                    binding?.contentShowKajian?.tvCategory?.text = category
                    binding?.contentShowKajian?.tvMosqueAddress?.text = mosqueName
                    val uri = it["youtube_link"].toString()
                    binding?.contentShowKajian?.btnPlay?.setOnClickListener {
                        val i = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        startActivity(i)
                    }
                }

                if (it["img_resource"] != null) {
                    Glide.with(applicationContext)
                            .load(it["img_resource"].toString())
                            .into(binding?.contentShowKajian?.imgThumbnail!!)
                } else {
                    binding?.contentShowKajian?.imgThumbnail?.visibility = View.GONE
                    binding?.contentShowKajian?.btnPlay?.visibility = View.GONE
                }
                binding?.svKajian?.visibility = View.VISIBLE
            } else {
                binding?.progressMessage?.visibility = View.GONE
                binding?.errorMessage?.visibility = View.VISIBLE
                val errorMessage = """
                        Error!
                        [${it["code"]}]: ${it["message"]}
                    """.trimIndent()
                binding?.tvErrorMessage?.text = errorMessage
            }
        }
    }

    private fun deleteKajian() {
        val url = resources.getString(R.string.server) + "api/kajian/$id"
        val client = AsyncHttpClient()
        client.delete(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                Toast.makeText(this@ShowKajianActivity, resources.getString(R.string.delete_kajian_success_message), Toast.LENGTH_SHORT).show()
                this@ShowKajianActivity.setResult(RESULT_DELETE, Intent())
                finish()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                Toast.makeText(this@ShowKajianActivity,
                        "Oops, an error occured!\n[$statusCode] ${error.message}\n${responseBody.toString()}",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_kajian, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.menuEdit -> {
                val i = Intent(this, AddUpdateKajianActivity::class.java)
                i.putExtra(AddUpdateKajianActivity.EXTRA_PARCEL_KAJIAN, kajian)
                startActivityForResult(i, AddUpdateKajianActivity.RESULT_UPDATE)
            }
            R.id.menuDelete -> {
                val alert = AlertDialog.Builder(this)
                alert.setTitle(resources.getString(R.string.sure))
                        .setMessage(resources.getString(R.string.delete_kajian_message) + " $title?")
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            deleteKajian()
                        }
                        .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                            /* no-op */
                        }
                        .setCancelable(false)
                val alertDialog = alert.create()
                alertDialog.show()
            }
        }
        return true
    }
}