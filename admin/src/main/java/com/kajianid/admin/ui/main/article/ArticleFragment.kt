package com.kajianid.admin.ui.main.article

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
import com.kajianid.admin.databinding.FragmentArticleBinding
import com.kajianid.admin.ui.article.AddUpdateArticleActivity
import com.kajianid.admin.ui.article.ReadArticleActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private var adapter: ArticleAdapter = ArticleAdapter()
    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ArticleAdapter()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.adapter = adapter

        binding.pullToRefresh.visibility = View.GONE
        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch(Dispatchers.Main) {
                val deferredStatus = async(Dispatchers.IO) {
                    articleViewModel.setArticle(requireActivity().applicationContext,
                            binding.etSearchArticle.text.toString())
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

        binding.etSearchArticle.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                fetchArticles()
            }

            true
        }

        articleViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ArticleViewModel::class.java)
        articleViewModel.article.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.setArticles(it)
                binding.pullToRefresh.visibility = View.VISIBLE
                binding.progressMessage.visibility = View.GONE
            }
        })
        articleViewModel.setArticleAsync(requireActivity(), adapter, "")

        binding.extendedFab.setOnClickListener {
            val i = Intent(activity?.applicationContext, AddUpdateArticleActivity::class.java)
            startActivityForResult(i, AddUpdateArticleActivity.RESULT_SAVE)
        }
    }

    private fun fetchArticles() {
        binding.pullToRefresh.visibility = View.GONE
        binding.progressMessage.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            val deferredStatus = async(Dispatchers.IO) {
                articleViewModel.setArticle(requireActivity().applicationContext,
                        binding.etSearchArticle.text.toString())
            }
            val status = deferredStatus.await()
            if (status != null) {
                binding.pullToRefresh.visibility = View.VISIBLE
                binding.progressMessage.visibility = View.GONE
                val parse = JSONObject(status)
                Toast.makeText(context, parse.getString("message"), Toast.LENGTH_SHORT).show()
            } else {
                binding.pullToRefresh.visibility = View.VISIBLE
                binding.progressMessage.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (resultCode) {
            AddUpdateArticleActivity.RESULT_SAVE -> fetchArticles()
            ReadArticleActivity.RESULT_DELETE -> fetchArticles()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}