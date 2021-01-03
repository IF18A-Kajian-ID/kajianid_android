package com.kajianid.ustadz.ui.mosque

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
import com.kajianid.ustadz.R
import com.kajianid.ustadz.data.Mosque
import com.kajianid.ustadz.databinding.FragmentMosqueListBinding
import com.kajianid.ustadz.prefs.CredentialPreference
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class MosqueListFragment : Fragment() {

    private var mosqueListAdapter: MosqueChooserAdapter = MosqueChooserAdapter()
    private lateinit var mosqueListViewModel: MosqueChooserViewModel
    private lateinit var binding: FragmentMosqueListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mosqueListAdapter = MosqueChooserAdapter()
        mosqueListAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentMosqueListBinding.inflate(inflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.rvMosque.layoutManager = LinearLayoutManager(context)
        binding.rvMosque.adapter = mosqueListAdapter

        mosqueListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MosqueChooserViewModel::class.java)
        mosqueListViewModel.mosques.observe(viewLifecycleOwner, {
            if (it != null) {
                mosqueListAdapter.setMosques(it)
                binding.progressMessage.visibility = View.GONE
            }
        })
        context?.let { mosqueListViewModel.setMosqueAsync(it, mosqueListAdapter, "", true) }

        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                val deferredStatus = async(Dispatchers.IO) {
                    mosqueListViewModel.setMosques(requireActivity().applicationContext,
                            binding.etSearchMosque.text.toString(), true)
                }
                val status = deferredStatus.await()
                if (status != null) {
                    binding.pullToRefresh.isRefreshing = false
                    val parse = JSONObject(status)
                    Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    binding.pullToRefresh.isRefreshing = false
                    mosqueListAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.etSearchMosque.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.progressMessage.visibility = View.VISIBLE
                GlobalScope.launch(Dispatchers.Main) {
                    val deferredStatus = async(Dispatchers.IO) {
                        mosqueListViewModel.setMosques(requireActivity().applicationContext,
                                binding.etSearchMosque.text.toString(), true)
                    }
                    val status = deferredStatus.await()
                    if (status != null) {
                        binding.pullToRefresh.isRefreshing = false
                        val parse = JSONObject(status)
                        Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
                    } else {
                        binding.pullToRefresh.isRefreshing = false
                        mosqueListAdapter.notifyDataSetChanged()
                    }
                }
            }
            true
        }

        binding.fabAddMosque.setOnClickListener {
            val i = Intent(context, MosqueChooserActivity::class.java)
            i.putExtra(MosqueChooserActivity.EXTRA_IS_USTADZ_ONLY, false)
            startActivityForResult(i, MosqueChooserActivity.REQUEST_MOSQUE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            MosqueChooserActivity.REQUEST_MOSQUE -> {
                if (data != null) {
                    val credential = CredentialPreference(requireActivity().applicationContext!!)

                    val mosque = data.getParcelableExtra<Mosque>(MosqueChooserActivity.EXTRA_MOSQUE_RESULT)!!
                    val progressMsg = resources.getString(R.string.progress_add_mosque) + mosque.mosqueName
                    binding.progressMessage.visibility = View.VISIBLE
                    binding.tvProgressMessage.text = progressMsg

                    val mosqueId = mosque.id
                    val ustadzId = credential.credential.username

                    val api = resources.getString(R.string.server) + "api/mosques"
                    val client = AsyncHttpClient()

                    val params = RequestParams()
                    params.put("id", mosqueId)
                    params.put("ustadz_id", ustadzId)

                    client.post(api, params, object : AsyncHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                            Toast.makeText(context, resources.getString(R.string.add_mosque_success) + mosque.mosqueName, Toast.LENGTH_SHORT).show()
                            binding.progressMessage.visibility = View.VISIBLE
                            GlobalScope.launch(Dispatchers.Main) {
                                val deferredStatus = async(Dispatchers.IO) {
                                    mosqueListViewModel.setMosques(requireActivity().applicationContext,
                                            binding.etSearchMosque.text.toString(), true)
                                }
                                val status = deferredStatus.await()
                                if (status != null) {
                                    binding.pullToRefresh.isRefreshing = false
                                    val parse = JSONObject(status)
                                    Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
                                } else {
                                    binding.pullToRefresh.isRefreshing = false
                                    mosqueListAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                        override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                            Toast.makeText(context, resources.getString(R.string.add_mosque_failed) + mosque.mosqueName, Toast.LENGTH_SHORT).show()
                            binding.progressMessage.visibility = View.GONE
                        }

                    })
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}