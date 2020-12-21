package com.kajianid.ustadz.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.kajianid.ustadz.R
import com.kajianid.ustadz.databinding.ActivityReadArticleBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class ReadArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadArticleBinding
    private lateinit var readArticleViewModel: ReadArticleViewModel
    private var id: String? = ""
    private var title = ""
    private var content = ""
    private val isError = false

    companion object {
        const val EXTRA_ARTICLE_ID = "extra_article_id"
        const val RESULT_DELETE = 111
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.read_article)

        binding.svArticle.visibility = View.GONE
        binding.progressMessage.visibility = View.VISIBLE
        binding.errorMessage.visibility = View.GONE

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString(EXTRA_ARTICLE_ID)
            readArticleViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
                    .get(ReadArticleViewModel::class.java)
            readArticleViewModel.article.observe(this, {
                if (it["status"] == true) {
                    title = it["title"].toString()
                    content = it["content"].toString()
                    binding.contentReadArticle.tvArticleTitle.text = it["title"].toString()
                    if (it["hasImg"] == true) {
                        Glide.with(this)
                                .load(
                                        getString(R.string.server) +
                                                "assets/articles/" + id + "." +
                                                Objects.requireNonNull(it["extension"]).toString()
                                )
                                .into(binding.contentReadArticle.imgArticle)
                    } else {
                        binding.contentReadArticle.imgArticle.visibility = View.GONE
                    }
                    val likes = it["like"].toString()
                    binding.contentReadArticle.tvLikeCount.text = likes
                    binding.contentReadArticle.tvPostDate.text = it["post_date"].toString()
                    binding.contentReadArticle.tvPostContent.text = it["content"].toString()
                    binding.progressMessage.visibility = View.GONE
                    binding.errorMessage.visibility = View.GONE
                    binding.svArticle.visibility = View.VISIBLE
                } else {
                    binding.progressMessage.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                    Error!
                    [${it["code"]}]: ${it["message"]}
                    """.trimIndent()
                    binding.tvErrorMessage.text = errorMessage
                }
                readArticleViewModel.setArticleAsync(this, id)
            })
        }

        binding.pullToRefresh.setOnRefreshListener {
            binding.svArticle.visibility = View.GONE
            binding.pullToRefresh.isRefreshing = false
            binding.errorMessage.visibility = View.GONE
            binding.progressMessage.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.Main) {
                val deferredArticles = async(Dispatchers.IO) {
                    id.let { readArticleViewModel.setArticle(applicationContext, it) }
                }
                val it = deferredArticles.await()
                if (it["status"] == true) {
                    title = it["title"].toString()
                    content = it["content"].toString()
                    binding.contentReadArticle.tvArticleTitle.text = it["title"].toString()
                    if (it["hasImg"] == true) {
                        Glide.with(applicationContext)
                                .load(resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}")
                                .into(binding.contentReadArticle.imgArticle)
                    } else {
                        binding.contentReadArticle.imgArticle.visibility = View.GONE
                    }
                    val likes = it["like"].toString() + " ${resources.getString(R.string.likes)}"
                    binding.contentReadArticle.tvLikeCount.text = likes
                    binding.contentReadArticle.tvPostDate.text = it["post_date"].toString()
                    binding.contentReadArticle.tvPostContent.text = it["content"].toString()
                    binding.progressMessage.visibility = View.GONE
                    binding.svArticle.visibility = View.VISIBLE
                } else {
                    binding.progressMessage.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                            Error!
                            [${it["code"]}]: ${it["message"]}
                        """.trimIndent()
                    binding.tvErrorMessage.text = errorMessage
                }
            }
        }

        binding.btnRefresh.setOnClickListener { _ ->
            binding.svArticle.visibility = View.GONE
            binding.errorMessage.visibility = View.GONE
            binding.progressMessage.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.Main) {
                val deferredArticles = async(Dispatchers.IO) {
                    id.let { readArticleViewModel.setArticle(applicationContext, it) }
                }
                val it = deferredArticles.await()
                if (it["status"] == true) {
                    title = it["title"].toString()
                    content = it["content"].toString()
                    binding.contentReadArticle.tvArticleTitle.text = it["title"].toString()
                    if (it["hasImg"] == true) {
                        Glide.with(applicationContext)
                                .load(resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}")
                                .into(binding.contentReadArticle.imgArticle)
                    } else {
                        binding.contentReadArticle.imgArticle.visibility = View.GONE
                    }
                    val likes = it["like"].toString() + " ${resources.getString(R.string.likes)}"
                    binding.contentReadArticle.tvLikeCount.text = likes
                    binding.contentReadArticle.tvPostDate.text = it["post_date"].toString()
                    binding.contentReadArticle.tvPostContent.text = it["content"].toString()
                    binding.progressMessage.visibility = View.GONE
                    binding.svArticle.visibility = View.VISIBLE
                } else {
                    binding.progressMessage.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                            Error!
                            [${it["code"]}]: ${it["message"]}
                        """.trimIndent()
                    binding.tvErrorMessage.text = errorMessage
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> super.onBackPressed()
            R.id.menuEdit -> {
                if (isError) {
                    Toast.makeText(this, resources.getString(R.string.update_error), Toast.LENGTH_SHORT).show()
                } else {
                    val i = Intent(this, AddUpdateArticleActivity::class.java)
                    i.putExtra(AddUpdateArticleActivity.EXTRA_ARTICLE_ID, id)
                    i.putExtra(AddUpdateArticleActivity.EXTRA_ARTICLE_TITLE, title)
                    i.putExtra(AddUpdateArticleActivity.EXTRA_ARTICLE_CONTENT, content)
                    startActivityForResult(i, AddUpdateArticleActivity.RESULT_UPDATE)
                }
            }
            R.id.menuDelete -> {
                val alert = AlertDialog.Builder(this)
                alert.setTitle(resources.getString(R.string.sure))
                        .setMessage(resources.getString(R.string.delete_article_message) + " $title?")
                        .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                            deleteArticle()
                        }
                        .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                            /* no-op */
                        }
                        .setCancelable(false)
                val alertDialog = alert.create()
                alertDialog.show()
            }
        }
        return true
    }

    private fun deleteArticle() {
        val url = resources.getString(R.string.server) + "api/articles/$id"
        val client = AsyncHttpClient()
        client.delete(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                Toast.makeText(this@ReadArticleActivity, resources.getString(R.string.delete_success_message), Toast.LENGTH_SHORT).show()
                this@ReadArticleActivity.setResult(RESULT_DELETE, Intent())
                finish()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable) {
                Toast.makeText(this@ReadArticleActivity,
                        "Oops, an error occured!\n[$statusCode] ${error.message}\n${responseBody.toString()}",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_kajian, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == AddUpdateArticleActivity.RESULT_UPDATE) {
            binding.svArticle.visibility = View.GONE
            binding.errorMessage.visibility = View.GONE
            binding.progressMessage.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.Main) {
                val deferredArticles = async(Dispatchers.IO) {
                    id.let { readArticleViewModel.setArticle(applicationContext, it) }
                }
                val it = deferredArticles.await()
                if (it["status"] == true) {
                    title = it["title"].toString()
                    content = it["content"].toString()
                    binding.contentReadArticle.tvArticleTitle.text = it["title"].toString()
                    if (it["hasImg"] == true) {
                        Glide.with(applicationContext)
                                .load(resources.getString(R.string.server) + "assets/articles/${id}.${it["extension"]}")
                                .into(binding.contentReadArticle.imgArticle)
                    } else {
                        binding.contentReadArticle.imgArticle.visibility = View.GONE
                    }
                    val likes = it["like"].toString() + " ${resources.getString(R.string.likes)}"
                    binding.contentReadArticle.tvLikeCount.text = likes
                    binding.contentReadArticle.tvPostDate.text = it["post_date"].toString()
                    binding.contentReadArticle.tvPostContent.text = it["content"].toString()
                    binding.progressMessage.visibility = View.GONE
                    binding.svArticle.visibility = View.VISIBLE
                } else {
                    binding.progressMessage.visibility = View.GONE
                    binding.errorMessage.visibility = View.VISIBLE
                    val errorMessage = """
                            Error!
                            [${it["code"]}]: ${it["message"]}
                        """.trimIndent()
                    binding.tvErrorMessage.text = errorMessage
                }
            }
        }
    }
}