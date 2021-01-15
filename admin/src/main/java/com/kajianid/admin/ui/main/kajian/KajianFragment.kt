package com.kajianid.admin.ui.main.kajian

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.admin.databinding.FragmentKajianBinding
import com.kajianid.admin.ui.kajian.ShowKajianActivity
import com.kajianid.admin.ui.main.kajian.KajianViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class KajianFragment : Fragment() {
    private var adapter = KajianAdapter()
    private lateinit var kajianViewModel: KajianViewModel
    private lateinit var binding: FragmentKajianBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = KajianAdapter()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentKajianBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // recview adapter
        binding.rvInfo.layoutManager = LinearLayoutManager(context)
        binding.rvInfo.adapter = adapter

        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                val deferredStatus = async(Dispatchers.IO) {
                    kajianViewModel.setKajian(requireActivity().applicationContext,
                            binding.etSearchKajian.text.toString())
                }
                val status = deferredStatus.await()
                if (status != null) {
                    binding.pullToRefresh.isRefreshing = false
                    val parse = JSONObject(status)
                    Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    binding.pullToRefresh.isRefreshing = false
                    adapter.notifyDataSetChanged()
                }
            }
        }

        binding.etSearchKajian.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.progressMessage.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    val deferredStatus = async(Dispatchers.IO) {
                        kajianViewModel.setKajian(requireActivity().applicationContext,
                                binding.etSearchKajian.text.toString())
                    }
                    val status = deferredStatus.await()
                    if (status != null) {
                        binding.progressMessage.visibility = View.GONE
                        val parse = JSONObject(status)
                        Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        binding.progressMessage.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            true
        }

        kajianViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(KajianViewModel::class.java)
        kajianViewModel.kajian.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.setData(it)
                binding.progressMessage.visibility = View.GONE
            }
        })
        kajianViewModel.setKajianAsync(requireActivity().applicationContext, adapter, "")

        binding.fabAddKajian.setOnClickListener {
            val i = Intent(activity?.applicationContext, com.kajianid.admin.ui.kajian.AddUpdateKajianActivity::class.java)
            startActivityForResult(i, com.kajianid.admin.ui.kajian.AddUpdateKajianActivity.RESULT_SAVE)
        }
    }

    private fun fetchKajian() {
        binding.progressMessage.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            val deferredStatus = async(Dispatchers.IO) {
                kajianViewModel.setKajian(requireActivity().applicationContext,
                        binding.etSearchKajian.text.toString())
            }
            val status = deferredStatus.await()
            if (status != null) {
                binding.progressMessage.visibility = View.GONE
                val parse = JSONObject(status)
                Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
            } else {
                binding.progressMessage.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            com.kajianid.admin.ui.kajian.AddUpdateKajianActivity.RESULT_SAVE -> fetchKajian()
            ShowKajianActivity.RESULT_DELETE -> fetchKajian()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}