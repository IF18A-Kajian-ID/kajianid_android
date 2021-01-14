package com.kajianid.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kajianid.android.adapter.ArticleSQLAdapter
import com.kajianid.android.databases.article.DbArticleHelper
import com.kajianid.android.databases.article.MappingHelper
import com.kajianid.android.databinding.FragmentDownloadedArticleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DownloadedArticleFragment : Fragment() {
    private lateinit var articleSQLAdapter: ArticleSQLAdapter
    private lateinit var dbArticleHelper: DbArticleHelper
    private lateinit var binding: FragmentDownloadedArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleSQLAdapter = ArticleSQLAdapter(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDownloadedArticleBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvArticles.layoutManager = LinearLayoutManager(context)
        binding.rvArticles.adapter = articleSQLAdapter
        binding.rvArticles.setHasFixedSize(true)//ukuran tidak aka berubah jk diputar

        //manggil database(buka)
        dbArticleHelper = DbArticleHelper.getInstance(requireContext().applicationContext)
        dbArticleHelper.open()

        loadArticle()
    }

    private fun loadArticle() {
        GlobalScope.launch(Dispatchers.Main){
            val deferredArticle = async (Dispatchers.IO){
                val cursor = dbArticleHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val article = deferredArticle.await()
            if(article.size > 0) articleSQLAdapter.setArticle(article)
            else{
                articleSQLAdapter.setArticle(ArrayList())
            }
        }

    }
    override fun onResume() {
        super.onResume()
        loadArticle()
    }


}