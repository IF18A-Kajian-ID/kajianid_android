package com.kajianid.android.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.android.R
import com.kajianid.android.adapter.KajianSQLAdapter
import com.kajianid.android.databases.kajian.DbKajianHelper
import com.kajianid.android.databases.kajian.MappingHelper
import com.kajianid.android.databinding.FragmentSavedKajianBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SavedKajianFragment : Fragment() {
    private lateinit var kajianSQLAdapter: KajianSQLAdapter
    private lateinit var dbKajianHelper: DbKajianHelper
    private lateinit var binding: FragmentSavedKajianBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedKajianBinding.inflate(layoutInflater)

        binding.rvKajian.layoutManager = LinearLayoutManager(context)
        binding.rvKajian.adapter = kajianSQLAdapter
        binding.rvKajian.setHasFixedSize(true) //tidak efek ketika diputar

        //memanggil database
        dbKajianHelper = DbKajianHelper.getInstance(requireContext().applicationContext)
        dbKajianHelper.open()

        loadKajian()
    }

    private fun loadKajian() {
        GlobalScope.launch ( Dispatchers.Main ) {
            val deferredKajian = async (Dispatchers.IO){
                val cursor = dbKajianHelper.queryAll()

                MappingHelper.mapCursorToArrayList(cursor)
            }
            val kajian = deferredKajian.await()
            if (kajian.size >0)kajianSQLAdapter.setKajian(kajian)
            else{
                kajianSQLAdapter.setKajian(ArrayList())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        kajianSQLAdapter = KajianSQLAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_kajian, container, false)
    }

    override fun onResume() {
        super.onResume()
        loadKajian()
    }
}