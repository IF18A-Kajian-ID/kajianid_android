package com.kajianid.android.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.android.R
import com.kajianid.android.adapter.ListKajianAdapter
import com.kajianid.android.databinding.ActivityKajianBinding
import com.kajianid.android.viewmodels.KajianViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class KajianActivity : AppCompatActivity() {
    var id = ""
    private lateinit var kajianViewModel: KajianViewModel
    private var listKajianAdapter: ListKajianAdapter = ListKajianAdapter(this)
    private var searchView: SearchView? = null
    private lateinit var binding: ActivityKajianBinding
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKajianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.VISIBLE

        // fungsi Toolbar dan ActionBar(back pd kajian)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(resources.getString(R.string.show_kajian))

        binding.recDataKajian.layoutManager = LinearLayoutManager(this)
        binding.recDataKajian.adapter = listKajianAdapter

        kajianViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(KajianViewModel::class.java)
        kajianViewModel.kajian.observe(this, Observer {
            if (it != null) {
                binding.progressBar.visibility = View.GONE
                listKajianAdapter.setKajian(it)
            }
        })
        kajianViewModel.setKajianAsync(this, listKajianAdapter, "")

        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch (Dispatchers.Main) {
                val deferredKajian = async(Dispatchers.IO) {
                    kajianViewModel.setKajian(this@KajianActivity, "")
                }
                val status = deferredKajian.await()
                if (status != null) {
                    val parse = JSONObject(status)
                    Toast.makeText(this@KajianActivity, parse.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    listKajianAdapter.notifyDataSetChanged()
                }
                binding.pullToRefresh.isRefreshing = false
            }
        }
    }
}