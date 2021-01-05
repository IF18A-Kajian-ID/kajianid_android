package com.kajianid.ustadz.ui.kajian

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.kajianid.ustadz.R
import com.kajianid.ustadz.data.Kajian
import com.kajianid.ustadz.data.Mosque
import com.kajianid.ustadz.databinding.ActivityAddUpdateKajianBinding
import com.kajianid.ustadz.prefs.CredentialPreference
import com.kajianid.ustadz.ui.mosque.MosqueChooserActivity
import com.kajianid.ustadz.utils.StringHelper
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import java.io.File
import java.io.FileNotFoundException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddUpdateKajianActivity : AppCompatActivity() {
    private val calendar = Calendar.getInstance()
    private var binding: ActivityAddUpdateKajianBinding? = null
    private var isEditMode = false
    private val kajian = Kajian()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateKajianBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.progressBar?.visibility = View.GONE
        binding?.errorMessage?.visibility = View.GONE
        setSupportActionBar(binding!!.toolbar)
        supportActionBar?.title = getString(R.string.add_kajian)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        if (bundle != null) {
            supportActionBar?.title = getString(R.string.update_kajian)
            val params = bundle.getParcelable<Parcelable>(EXTRA_PARCEL_KAJIAN) as Kajian?
            isEditMode = true
            kajian.id = params?.id
            kajian.title = params?.title
            kajian.date = params?.date
            kajian.address = params?.address
            kajian.place = params?.place
            kajian.description = params?.description
            kajian.ytLink = params?.ytLink
            kajian.imgResource = params?.imgResource
            kajian.mosqueId = params?.mosqueId
            kajian.mosqueName = params?.mosqueName
            when (kajian.place) {
                "Di Tempat" -> {
                    binding?.spKajianCategory?.setSelection(0)
                    binding?.rlKajianImage?.visibility = View.VISIBLE
                    binding?.tilKajianAddress?.visibility = View.VISIBLE
                    binding?.tilKajianYtLink?.visibility = View.GONE
                    if (kajian.imgResource != null) {
                        Glide.with(this)
                                .load(kajian.imgResource)
                                .into(binding!!.imgKajian)
                    }
                    binding?.edtKajianAddress?.setText(kajian.address)
                }
                "Video" -> {
                    binding?.spKajianCategory?.setSelection(1)
                    binding?.rlKajianImage?.visibility = View.GONE
                    binding?.tilKajianAddress?.visibility = View.GONE
                    binding?.tilKajianYtLink?.visibility = View.VISIBLE
                    binding?.edtKajianYtLink?.setText(kajian.ytLink)
                }
                "Live Streaming" -> {
                }
            }
            binding?.edtKajianTitle?.setText(kajian.title)
            binding?.tvMosqueId?.text = kajian.id
            binding?.tvMosqueName?.text = kajian.mosqueName
            binding?.edtKajianDescription?.setText(kajian.description)
            try {
                val dateParse = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(kajian.date)!!
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateParse)
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateParse)
                binding?.edtKajianDateDue?.setText(date)
                binding?.edtKajianTimeDue?.setText(time)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            binding?.spKajianCategory?.isEnabled = false
            binding?.btnChooseKajianImage?.isEnabled = false
        }
        binding?.btnChooseMosque?.setOnClickListener { it: View? ->
            val i = Intent(this, MosqueChooserActivity::class.java)
            i.putExtra(MosqueChooserActivity.EXTRA_IS_USTADZ_ONLY, true)
            startActivityForResult(i, MosqueChooserActivity.REQUEST_MOSQUE)
        }
        binding?.btnChooseKajianImage?.setOnClickListener { it: View? ->
            if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                val i = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                )
                startActivity(i)
            } else {
                Toast.makeText(
                        this,
                        getString(R.string.file_permission_denied),
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        binding?.btnRetryError?.setOnClickListener { it: View? ->
            try {
                postData()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        binding?.btnCancelError?.setOnClickListener { it: View? ->
            binding?.progressBar?.visibility = View.GONE
            binding?.errorMessage?.visibility = View.GONE
        }
        binding?.spKajianCategory?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position == 0) {
                    binding?.rlKajianImage?.visibility = View.VISIBLE
                    binding?.tilKajianAddress?.visibility = View.VISIBLE
                    binding?.tilKajianYtLink?.visibility = View.GONE
                } else {
                    binding?.rlKajianImage?.visibility = View.GONE
                    binding?.tilKajianAddress?.visibility = View.GONE
                    binding?.tilKajianYtLink?.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // no-op
            }
        }
        val dateSetListener = OnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = month
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            val dateFormat = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
            binding?.edtKajianDateDue?.setText(sdf.format(calendar.time))
        }
        binding?.edtKajianDateDue?.setOnClickListener { it: View? ->
            val dpd = DatePickerDialog(
                    this,
                    dateSetListener,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
            )
            dpd.setTitle(getString(R.string.choose_date_due))
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000 // minimal hari ini
            dpd.show()
        }
        binding?.edtKajianTimeDue?.setOnClickListener { it: View? ->
            val mCurrentTime = Calendar.getInstance()
            val hourNow = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minuteNow = mCurrentTime[Calendar.MINUTE]
            val timeSetListener = OnTimeSetListener { view: TimePicker?, hourOfDay: Int, minute: Int ->
                val timeCalendarSet = Calendar.getInstance()
                timeCalendarSet[Calendar.HOUR_OF_DAY] = hourOfDay
                timeCalendarSet[Calendar.MINUTE] = minute
                val timeSet = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeCalendarSet.time)
                binding?.edtKajianTimeDue?.setText(timeSet)
            }
            val tpd = TimePickerDialog(
                    this,
                    timeSetListener,
                    hourNow,
                    minuteNow,
                    true
            )
            tpd.setTitle(getString(R.string.choose_time_due))
            tpd.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_update, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed() else if (item.itemId == R.id.menuSave) {
            try {
                postData()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            MosqueChooserActivity.REQUEST_MOSQUE -> if (data != null) {
                val mosque = data.getParcelableExtra<Mosque>(MosqueChooserActivity.EXTRA_MOSQUE_RESULT)!!
                binding?.tvMosqueId?.text = mosque.id.toString()
                binding?.tvMosqueName?.text = mosque.mosqueName
            }
            RESULT_OK -> if (requestCode == RESULT_LOAD_IMAGE && data != null) {
                val selectedImage = data.data
                Glide.with(this)
                        .load(selectedImage)
                        .into(binding?.imgKajian!!)
                val path = selectedImage!!.path!!.substring(5)
                binding?.tvKajianImagePath?.text = path
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(FileNotFoundException::class)
    private fun postData() {
        if (isEditMode) {
            // save
            val selectedItem = binding!!.spKajianCategory.selectedItem.toString()
            val categories = resources.getStringArray(R.array.kajian_category_entries)
            var kajianTitle: String?
            var kajianAddress: String? = null
            var kajianYtLink: String? = null
            var kajianDescription: String?
            var kajianDateDue: String?
            var kajianTimeDue: String?
            if (selectedItem == categories[0]) {
                if (StringHelper.isNullOrEmpty(binding!!.edtKajianAddress.text.toString())) {
                    Toast.makeText(this, getString(R.string.kajian_address_empty), Toast.LENGTH_SHORT).show()
                    binding!!.edtKajianAddress.requestFocus()
                    return
                }
            } else {
                if (StringHelper.isNullOrEmpty(binding!!.edtKajianYtLink.text.toString())) {
                    Toast.makeText(this, getString(R.string.kajian_yt_link_empty), Toast.LENGTH_SHORT).show()
                    binding!!.edtKajianAddress.requestFocus()
                    return
                }
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianTitle.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_title_empty), Toast.LENGTH_SHORT).show()
                binding!!.edtKajianTitle.requestFocus()
                return
            }
            if (binding!!.tvMosqueId.text.toString() == getString(R.string.example_id)) {
                Toast.makeText(this, getString(R.string.kajian_mosque_empty), Toast.LENGTH_SHORT).show()
                binding!!.btnChooseMosque.requestFocus()
                return
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianDescription.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_desc_empty), Toast.LENGTH_SHORT).show()
                binding!!.edtKajianTitle.requestFocus()
                return
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianDateDue.text.toString()) ||
                    StringHelper.isNullOrEmpty(binding!!.edtKajianTimeDue.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_due_empty), Toast.LENGTH_SHORT).show()
                binding!!.svAddUpdateKajian.fullScroll(ScrollView.FOCUS_DOWN)
                return
            }
            binding!!.progressMessage.visibility = View.VISIBLE
            binding!!.errorMessage.visibility = View.GONE
            val kajianCategory: String
            kajianCategory = if (selectedItem == categories[0]) "Di Tempat" else if (selectedItem == categories[1]) "Video" else if (selectedItem == categories[2]) "Live Streaming" else throw IllegalArgumentException("Invalid Kajian Category!")
            kajianTitle = binding!!.edtKajianTitle.text.toString()
            if (selectedItem == categories[0]) kajianAddress = binding!!.edtKajianAddress.text.toString() else kajianYtLink = binding!!.edtKajianYtLink.text.toString()
            val mosqueId = binding!!.tvMosqueId.text.toString().toInt()
            kajianDescription = binding!!.edtKajianDescription.text.toString()
            kajianDateDue = binding!!.edtKajianDateDue.text.toString()
            kajianTimeDue = binding!!.edtKajianTimeDue.text.toString()
            val kajianDateTimeDue = "$kajianDateDue $kajianTimeDue"
            val api = getString(R.string.server) + "api/kajian"
            val client = AsyncHttpClient()
            val credential = CredentialPreference(this)
            val params = RequestParams()
            params.put("title", kajianTitle)
            params.put("ustadz_id", credential.credential.username)
            params.put("mosque_id", mosqueId)
            if (selectedItem == categories[0]) {
                params.put("address", kajianAddress)
                if (binding!!.tvKajianImagePath.text.toString() == getString(R.string.example_path)) {
                    val file = File(binding!!.tvKajianImagePath.text.toString())
                    if (file.isFile) params.put("file", file) else Toast.makeText(this, "Not found: " + file.absolutePath, Toast.LENGTH_LONG).show()
                }
            } else params.put("yt_url", kajianYtLink)
            params.put("place", kajianCategory)
            params.put("desc", kajianDescription)
            params.put("due", kajianDateTimeDue)
            client.setTimeout(60000)
            client.post(api, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    binding!!.progressMessage.visibility = View.GONE
                    binding!!.errorMessage.visibility = View.GONE
                    Toast.makeText(this@AddUpdateKajianActivity, getString(R.string.add_kajian_success), Toast.LENGTH_SHORT).show()
                    this@AddUpdateKajianActivity.setResult(RESULT_SAVE)
                    finish()
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    binding!!.progressMessage.visibility = View.GONE
                    binding!!.errorMessage.visibility = View.VISIBLE
                    val errorMsg = """
                        Oops! An error occurred.
                        [$statusCode]
                        ${Arrays.toString(responseBody)}
                        """.trimIndent()
                    binding!!.tvErrorMessage.text = errorMsg
                }
            })
        } else {
            val selectedItem = binding!!.spKajianCategory.selectedItem.toString()
            val categories = resources.getStringArray(R.array.kajian_category_entries)
            var kajianTitle: String?
            var kajianAddress: String? = null
            var kajianYtLink: String? = null
            var kajianDescription: String?
            var kajianDateDue: String?
            var kajianTimeDue: String?
            if (selectedItem == categories[0]) {
                if (StringHelper.isNullOrEmpty(binding!!.edtKajianAddress.text.toString())) {
                    Toast.makeText(this, getString(R.string.kajian_address_empty), Toast.LENGTH_SHORT).show()
                    binding!!.edtKajianAddress.requestFocus()
                    return
                }
            } else {
                if (StringHelper.isNullOrEmpty(binding!!.edtKajianYtLink.text.toString())) {
                    Toast.makeText(this, getString(R.string.kajian_yt_link_empty), Toast.LENGTH_SHORT).show()
                    binding!!.edtKajianAddress.requestFocus()
                    return
                }
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianTitle.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_title_empty), Toast.LENGTH_SHORT).show()
                binding!!.edtKajianTitle.requestFocus()
                return
            }
            if (binding!!.tvMosqueId.text.toString() == getString(R.string.example_id)) {
                Toast.makeText(this, getString(R.string.kajian_mosque_empty), Toast.LENGTH_SHORT).show()
                binding!!.btnChooseMosque.requestFocus()
                return
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianDescription.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_desc_empty), Toast.LENGTH_SHORT).show()
                binding!!.edtKajianTitle.requestFocus()
                return
            }
            if (StringHelper.isNullOrEmpty(binding!!.edtKajianDateDue.text.toString()) ||
                    StringHelper.isNullOrEmpty(binding!!.edtKajianTimeDue.text.toString())) {
                Toast.makeText(this, getString(R.string.kajian_due_empty), Toast.LENGTH_SHORT).show()
                binding!!.svAddUpdateKajian.fullScroll(ScrollView.FOCUS_DOWN)
                return
            }
            binding!!.progressMessage.visibility = View.VISIBLE
            binding!!.errorMessage.visibility = View.GONE
            val kajianCategory: String
            kajianCategory = if (selectedItem == categories[0]) "Di Tempat" else if (selectedItem == categories[1]) "Video" else if (selectedItem == categories[2]) "Live Streaming" else throw IllegalArgumentException("Invalid Kajian Category!")
            kajianTitle = binding!!.edtKajianTitle.text.toString()
            if (selectedItem == categories[0]) kajianAddress = binding!!.edtKajianAddress.text.toString() else kajianYtLink = binding!!.edtKajianYtLink.text.toString()
            val mosqueId = binding!!.tvMosqueId.text.toString().toInt()
            kajianDescription = binding!!.edtKajianDescription.text.toString()
            kajianDateDue = binding!!.edtKajianDateDue.text.toString()
            kajianTimeDue = binding!!.edtKajianTimeDue.text.toString()
            val kajianDateTimeDue = "$kajianDateDue $kajianTimeDue"
            val api = getString(R.string.server) + "api/kajian/" + kajian.id
            val client = AsyncHttpClient()
            val credential = CredentialPreference(this)
            val params = RequestParams()
            params.put("title", kajianTitle)
            params.put("ustadz_id", credential.credential.username)
            params.put("mosque_id", mosqueId)
            if (selectedItem == categories[0]) {
                params.put("address", kajianAddress)
                if (binding!!.tvKajianImagePath.text.toString() == getString(R.string.example_path)) {
                    val file = File(binding!!.tvKajianImagePath.text.toString())
                    if (file.isFile) params.put("file", file) else Toast.makeText(this, "Not found: " + file.absolutePath, Toast.LENGTH_LONG).show()
                }
            } else params.put("yt_url", kajianYtLink)
            params.put("place", kajianCategory)
            params.put("desc", kajianDescription)
            params.put("due", kajianDateTimeDue)
            client.setTimeout(60000)
            client.put(api, params, object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    binding!!.progressMessage.visibility = View.GONE
                    binding!!.errorMessage.visibility = View.GONE
                    Toast.makeText(this@AddUpdateKajianActivity, getString(R.string.add_kajian_success), Toast.LENGTH_SHORT).show()
                    this@AddUpdateKajianActivity.setResult(RESULT_UPDATE)
                    finish()
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    binding!!.progressMessage.visibility = View.GONE
                    binding!!.errorMessage.visibility = View.VISIBLE
                    val errorMsg = """
                        Oops! An error occurred.
                        [$statusCode]
                        ${String(responseBody)}
                        """.trimIndent()
                    binding!!.tvErrorMessage.text = errorMsg
                }
            })
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    companion object {
        private const val RESULT_LOAD_IMAGE = 111
        const val RESULT_SAVE = 130
        const val RESULT_UPDATE = 260
        const val EXTRA_PARCEL_KAJIAN = "extra_parcel_kajian"
    }
}