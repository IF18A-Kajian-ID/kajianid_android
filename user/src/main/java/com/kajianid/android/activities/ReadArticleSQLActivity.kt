package com.kajianid.android.activities

import android.content.ContentValues
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import com.bumptech.glide.Glide
import com.kajianid.android.databases.article.MappingHelper
import com.kajianid.android.R
import com.kajianid.android.data.Article
import com.kajianid.android.databases.DatabaseContract
import com.kajianid.android.databases.article.DbArticleHelper
import com.kajianid.android.databinding.ActivityReadArticleBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ReadArticleSQLActivity: AppCompatActivity() {
    private lateinit var article: Article
    private var downloaded = false
    private lateinit var dbArticleHelper: DbArticleHelper
    private lateinit var binding: ActivityReadArticleBinding

    companion object {
        const val EXTRA_PARCEL_ARTICLES = "extra_parcel_articles"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.VISIBLE
        binding.progressMessage.visibility = View.GONE
        binding.errorMessage.visibility = View.GONE
        binding.ContentReadArticle.imgLike.visibility = View.GONE
        binding.ContentReadArticle.tvLikeCount.visibility = View.GONE

        val bundle = intent.extras!!
        article = bundle.getParcelable(EXTRA_PARCEL_ARTICLES)!!
        binding.ContentReadArticle.tvArticleTitle.text = article.title
        binding.ContentReadArticle.tvPostDate.text = article.postDate
        val ustadzName = resources.getString(R.string.by) + " " + article.ustadzName
        binding.ContentReadArticle.tvNamaUstad.text = ustadzName
        binding.ContentReadArticle.tvPostContent.text = article.content

        if (article.hasImg == "true") {
            Glide.with(this)
                    .load(article.imgUrl) //masih bug
                    .into(binding.ContentReadArticle.imgArticle)
        } else {
            binding.ContentReadArticle.imgArticle.visibility = View.GONE
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("Artikel Islami")

        dbArticleHelper = DbArticleHelper.getInstance(this)
        dbArticleHelper.open()

        GlobalScope.launch(Dispatchers.Main) {
            val deferredArticle = async(Dispatchers.IO) {
                val cursor = article.id?.let { dbArticleHelper.queryById(it) }
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val isDownload = deferredArticle.await()
            if (isDownload.size == 0) {
                downloaded = false
                binding.fabDownload.setImageResource(R.drawable.ic_baseline_assignment_returned_24)
            } else {
                downloaded = true
                binding.fabDownload.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
            }

        }

        binding.fabDownload.setOnClickListener {
            if (downloaded) {
                val alert = AlertDialog.Builder(this)
                alert.setTitle(resources.getString(R.string.sure))
                alert.setMessage(resources.getString(R.string.remove_article_confirm))
                alert.setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    article.id?.let { it1 -> dbArticleHelper.deleteById(it1) }
                    downloaded = false
                    binding.fabDownload.setImageResource(R.drawable.ic_baseline_assignment_returned_24)
                    Toast.makeText(this, "Artikel telah dihapus!", Toast.LENGTH_LONG).show()
                    finish()
                }
                alert.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                    /* no-op */
                }
                alert.create().show()
            } else {
                val values = ContentValues()
                values.put(DatabaseContract.ArticleColums.ID, article.id)
                values.put(DatabaseContract.ArticleColums.TITLE, article.title)
                values.put(DatabaseContract.ArticleColums.POST_DATE, article.postDate)
                values.put(DatabaseContract.ArticleColums.CONTENT, article.content)
                values.put(DatabaseContract.ArticleColums.HAS_IMG, article.hasImg)
                values.put(DatabaseContract.ArticleColums.USTADZ_NAME, article.ustadzName)
                values.put(DatabaseContract.ArticleColums.IMGURL, article.imgUrl)
                dbArticleHelper.insert(values)
                downloaded = true
                binding.fabDownload.setImageResource(R.drawable.ic_baseline_assignment_turned_in_24)
                Toast.makeText(this, "Artikel Berhasil Diunduh", Toast.LENGTH_LONG).show()
            }
            binding.pullToRefresh.setOnRefreshListener { binding.pullToRefresh.isRefreshing = false }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_article_kajian, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> super.onBackPressed()
            R.id.share -> {
                val mimeType = "text/plain"
                ShareCompat.IntentBuilder.from(this).apply {
                    setType(mimeType)
                    setChooserTitle("Bagikan artikel ini sekarang!")
                    setText("""
*${binding.ContentReadArticle.tvArticleTitle.text}*
_${binding.ContentReadArticle.tvNamaUstad.text} pada ${binding.ContentReadArticle.tvPostDate.text}_

${binding.ContentReadArticle.tvPostContent.text}

Informasi ini disebarkan melalui aplikasi ${getString(R.string.app_name)}.
                    """.trimIndent())
                    startChooser()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}