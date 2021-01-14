package com.kajianid.android.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.kajianid.android.databinding.ActivityMosqueLocationBinding
import com.kajianid.android.pageradapter.MosqueLocationPagerAdapter

class MosqueLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMosqueLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMosqueLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.elevation = 1f

        val mosqueLocationPagerAdapter = MosqueLocationPagerAdapter(this, supportFragmentManager)
        binding.vpMosqueLocation.adapter = mosqueLocationPagerAdapter
        binding.tlMosqueLocation.setupWithViewPager(binding.vpMosqueLocation)
        //tlMosqueLocation.getTabAt(0)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_city_36, theme)
        //tlMosqueLocation.getTabAt(1)?.icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_list_24, theme)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}