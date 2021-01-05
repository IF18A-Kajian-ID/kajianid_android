package com.kajianid.android

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.android.Adapter.ListArticleAdapter
import com.kajianid.android.databinding.ActivityArticleIslamiBinding
import com.kajianid.android.viewmodels.ArticleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class ArticleIslamiActivity : AppCompatActivity() {
    var id = ""
    private lateinit var articleViewModel: ArticleViewModel
    private var listArticleAdapter: ListArticleAdapter = ListArticleAdapter()
    private var searchView: SearchView? = null
    private lateinit var binding: ActivityArticleIslamiBinding
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleIslamiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.VISIBLE

        // fungsi Toolbar dan ActionBar(back pd artikel)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(resources.getString(R.string.read_article))

        binding.recDataArtikel.layoutManager = LinearLayoutManager(this)
        binding.recDataArtikel.adapter = listArticleAdapter
        articleViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ArticleViewModel::class.java)
        articleViewModel.article.observe(this, Observer {
            if (it != null) {
                binding.progressBar.visibility = View.GONE
                listArticleAdapter.setArticle(it)
            }
        })
        articleViewModel.setArticleAsync(this, listArticleAdapter, "")

        binding.pullToRefresh.setOnRefreshListener {
            GlobalScope.launch (Dispatchers.Main) {
                val deferredArticles = async(Dispatchers.IO) {
                    articleViewModel.setArticle(this@ArticleIslamiActivity, searchView?.query.toString())
                }
                val status = deferredArticles.await()
                if (status != null) {
                    val parse = JSONObject(status)
                    Toast.makeText(this@ArticleIslamiActivity, parse.getString("message"), Toast.LENGTH_SHORT).show()
                } else {
                    listArticleAdapter.notifyDataSetChanged()
                }
                binding.pullToRefresh.isRefreshing = false
            }
        }
    }
}