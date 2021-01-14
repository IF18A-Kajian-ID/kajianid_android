package com.kajianid.android.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.android.R
import com.kajianid.android.adapter.MosqueListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.kajianid.android.databinding.FragmentMosqueListBinding
import com.kajianid.android.viewmodels.MosqueListViewModel
import org.json.JSONObject

class MosqueListFragment : Fragment() {

    private var mosqueListAdapter = MosqueListAdapter()
    private lateinit var mosqueListViewModel: MosqueListViewModel
    private lateinit var binding: FragmentMosqueListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mosqueListAdapter = MosqueListAdapter()
        mosqueListAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mosque_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMosqueListBinding.inflate(layoutInflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = mosqueListAdapter

        binding.progressBar.visibility = View.VISIBLE

        mosqueListViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(MosqueListViewModel::class.java)
        mosqueListViewModel.getMosque().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                mosqueListAdapter.setData(it)
                binding.progressBar.visibility = View.GONE
            }
        })
        context?.let { mosqueListViewModel.setMosqueAsync(it, mosqueListAdapter, "") }

        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                val deferredStatus = async(Dispatchers.IO) {
                    mosqueListViewModel.setMosque(requireActivity().applicationContext,
                            "")
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_article_kajian_search, menu)

        // SearchView start
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.queryHint = getString(R.string.search_mosque)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.progressBar.visibility = View.VISIBLE
                mosqueListViewModel.setMosqueAsync(
                        requireContext(),
                        mosqueListAdapter,
                        query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /* no-op */
                return true
            }
        })
        // SearchView end


        super.onCreateOptionsMenu(menu, inflater)
    }
}