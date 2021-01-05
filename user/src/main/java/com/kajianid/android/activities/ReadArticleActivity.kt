package com.kajianid.android.activities

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kajianid.android.R
import com.kajianid.android.databinding.ActivityReadArticleBinding
import com.kajianid.android.viewmodels.ReadArticleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ReadArticleActivity : AppCompatActivity() {
    private lateinit var readArticleViewModel : ReadArticleViewModel
    private var id: String = ""
    private var title: String = ""
    private var content: String = ""
    private var isError: Boolean = false
    private var downloaded =  false
    private lateinit var dbArticleHelper: DbArticleHelper
    private lateinit var article:Article
    private lateinit var binding: ActivityReadArticleBinding

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.VISIBLE

        val bundle = intent.extras
        id = bundle?.get(EXTRA_ARTICLE_ID).toString()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(resources.getString(R.string.read_article))

        readArticleViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                .get(ReadArticleViewModel::class.java)
        readArticleViewModel.getArticle().observe(this, Observer {
            if (it["status"] == true) {
                title = it["title"].toString()
                content = it["content"].toString()
                tvArticleTitle.text = it["title"].toString()
                if (it["hasImg"] == true) {
                    Glide.with(this)
                            .load(resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}")
                            .into(im)
                } else {
                    imgArticle.visibility = View.GONE
                }
                val likes = it["like"].toString() + " ${resources.getString(R.string.likes)}"
                tvLikeCount.text = likes
                tvPostDate.text = it["post_date"].toString()
                tvPostContent.text = it["content"].toString()
                val ustadzName = resources.getString(R.string.by) + " " + it["ustadz_name"].toString()
                tv_nama_Ustad.text = ustadzName
                progressMessage.visibility = View.GONE
                errorMessage.visibility = View.GONE
                article = Article()
                article.id = it["id"].toString()
                article.title = it["title"].toString()
                article.content = it["content"].toString()
                article.post_date = it["post_date"].toString()
                article.ustadzName = it["ustadz_name"].toString()
                article.hasImg = it["hasImg"].toString()
                article.imgUrl = resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}"
                article.likes = it["like"].toString().toInt()
            } else {
                progressMessage.visibility = View.GONE
                errorMessage.visibility = View.VISIBLE
                val errorMessage = """
                    Error!
                    [${it["code"]}]: ${it["message"]}
                """.trimIndent()
                tvErrorMessage.text = errorMessage
            }
        })

        readArticleViewModel.setArticleAsync(this, id)
        dbArticleHelper= DbArticleHelper.getInstance(this)
        dbArticleHelper.open()

        GlobalScope.launch(Dispatchers.Main) {
            val deferredArticle = async(Dispatchers.IO) {
                val cursor = dbArticleHelper.queryById(id)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val isDownload = deferredArticle.await()
            if(isDownload.size==0){
                downloaded= false
                fabDownload.setImageResource(R.drawable.ic_baseline_assignment_returned_24)
            }else{
                downloaded= true
                fabDownload.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
            }
        }
        fabDownload.setOnClickListener {
            if(downloaded){
                val alert = AlertDialog.Builder(this)
                alert.setTitle(resources.getString(R.string.sure))
                alert.setMessage(resources.getString(R.string.remove_article_confirm))
                alert.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    dbArticleHelper.deleteById(id)
                    downloaded = false
                    fabDownload.setImageResource(R.drawable.ic_baseline_assignment_returned_24)
                    Toast.makeText(this, "Artikel telah dihapus!", Toast.LENGTH_LONG).show()
                }
                alert.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                    /* no-op */
                }
                alert.create().show()
            }else{
                val values= ContentValues()
                values.put(DatabaseContract.ArticleColums.ID, article.id)
                values.put(DatabaseContract.ArticleColums.TITLE, article.title)
                values.put(DatabaseContract.ArticleColums.POST_DATE, article.post_date)
                values.put(DatabaseContract.ArticleColums.CONTENT, article.content)
                values.put(DatabaseContract.ArticleColums.HAS_IMG, article.hasImg)
                values.put(DatabaseContract.ArticleColums.USTADZ_NAME, article.ustadzName)
                values.put(DatabaseContract.ArticleColums.IMGURL, article.imgUrl)
                dbArticleHelper.insert(values)
                downloaded= true
                fabDownload.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
                Toast.makeText(this, "Artikel Berhasil Diunduh", Toast.LENGTH_LONG).show()
            }
        }
        // pullToRefresh
        pullToRefresh.setOnRefreshListener {
            svArticle.visibility = View.GONE
            pullToRefresh.isRefreshing = false
            errorMessage.visibility = View.GONE
            progressMessage.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.Main) {
                val deferredArticles = async(Dispatchers.IO) {
                    id.let { readArticleViewModel.setArticle(applicationContext, it) }
                }
                val it = deferredArticles.await()
                if (it["status"] == true) {
                    title = it["title"].toString()
                    content = it["content"].toString()
                    tvArticleTitle.text = it["title"].toString()
                    if (it["hasImg"] == true) {
                        Glide.with(applicationContext)
                                .load(resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}")
                                .into(imgArticle)
                    } else {
                        imgArticle.visibility = View.GONE
                    }
                    val likes = it["like"].toString() + " ${resources.getString(R.string.likes)}"
                    tvLikeCount.text = likes
                    tvPostDate.text = it["post_date"].toString()
                    tvPostContent.text = it["content"].toString()
                    progressMessage.visibility = View.GONE
                    svArticle.visibility = View.VISIBLE
                } else {
                    progressMessage.visibility = View.GONE
                    errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                            Error!
                            [${it["code"]}]: ${it["message"]}
                        """.trimIndent()
                    tvErrorMessage.text = errorMessage
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }







}


}