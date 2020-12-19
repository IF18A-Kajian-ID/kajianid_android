package com.kajianid.ustadz.ui.article;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.databinding.ActivityAddUpdateArticleBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class AddUpdateArticleActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE_ID = "extra_article_id";
    public static final String EXTRA_ARTICLE_TITLE = "extra_article_title";
    public static final String EXTRA_ARTICLE_CONTENT = "extra_article_content";
    public static final int RESULT_SAVE = 130;
    public static final int RESULT_UPDATE = 260;

    private String id = "";
    private boolean isProgress = false;

    private ActivityAddUpdateArticleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateArticleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressMessage.setVisibility(View.GONE);
        binding.errorMessage.setVisibility(View.GONE);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString(EXTRA_ARTICLE_ID, "");
            String title = bundle.getString(EXTRA_ARTICLE_TITLE, "");
            String content = bundle.getString(EXTRA_ARTICLE_CONTENT, "");

            String titleWindow;
            if (StringHelper.isNullOrEmpty(id)) {
                titleWindow = getString(R.string.add_article);
                binding.edtArticleTitle.setText("");
                binding.edtArticleContent.setText("");
            } else {
                titleWindow = getString(R.string.update_article);
                binding.edtArticleTitle.setText(title);
                binding.edtArticleContent.setText(content);
            }
            getSupportActionBar().setTitle(titleWindow);
        }

        binding.btnCancelError.setOnClickListener(it -> {
            isProgress = false;
            binding.progressMessage.setVisibility(View.GONE);
            binding.errorMessage.setVisibility(View.GONE);
        });

        binding.btnRetryError.setOnClickListener(it -> saveArticle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_update, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        } else if (item.getItemId() == R.id.menuSave) {
            saveArticle();
        }
        return true;
    }

    private void saveArticle() {
        if (StringHelper.isNullOrEmpty(id)) {
            isProgress = true;

            binding.progressMessage.setVisibility(View.VISIBLE);
            binding.tvProgressMessage.setText(R.string.save_please_wait);
            binding.errorMessage.setVisibility(View.GONE);

            String articleTitle = binding.edtArticleTitle.getText().toString();
            String articleContent = binding.edtArticleContent.getText().toString();

            if (isNullOrEmpty(articleTitle)) {
                Toast.makeText(this, getString(R.string.title_must_fill), Toast.LENGTH_SHORT).show();
                binding.progressMessage.setVisibility(View.GONE);
                binding.errorMessage.setVisibility(View.GONE);
                isProgress = false;
            } else if (isNullOrEmpty(articleContent)) {
                Toast.makeText(this, getString(R.string.content_must_fill), Toast.LENGTH_SHORT).show();
                binding.progressMessage.setVisibility(View.GONE);
                binding.errorMessage.setVisibility(View.GONE);
                isProgress = false;
            } else {
                String url = getString(R.string.server) + "api/articles";
                CredentialPreference credential = new CredentialPreference(this);
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = new RequestParams();
                params.put("title", articleTitle);
                params.put("content", articleContent);
                params.put("ustadz_id", credential.getCredential().getUsername());

                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        binding.progressMessage.setVisibility(View.GONE);
                        binding.errorMessage.setVisibility(View.GONE);
                        Toast.makeText(AddUpdateArticleActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();

                        AddUpdateArticleActivity.this.setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        binding.progressMessage.setVisibility(View.GONE);
                        binding.errorMessage.setVisibility(View.VISIBLE);
                        String errorMessage = "Oops! An error occured.\n"
                                + "[" + statusCode + "] "
                                + error.getMessage();
                        binding.tvErrorMessage.setText(errorMessage);
                    }
                });
            }
        } else {
            isProgress = true;

            binding.progressMessage.setVisibility(View.VISIBLE);
            binding.tvProgressMessage.setText(R.string.save_please_wait);
            binding.errorMessage.setVisibility(View.GONE);

            String articleTitle = binding.edtArticleTitle.getText().toString();
            String articleContent = binding.edtArticleContent.getText().toString();

            if (isNullOrEmpty(articleTitle)) {
                Toast.makeText(this, getString(R.string.title_must_fill), Toast.LENGTH_SHORT).show();
                binding.progressMessage.setVisibility(View.GONE);
                binding.errorMessage.setVisibility(View.GONE);
                isProgress = false;
            } else if (isNullOrEmpty(articleContent)) {
                Toast.makeText(this, getString(R.string.content_must_fill), Toast.LENGTH_SHORT).show();
                binding.progressMessage.setVisibility(View.GONE);
                binding.errorMessage.setVisibility(View.GONE);
                isProgress = false;
            } else {
                String url = getString(R.string.server) + "api/articles/" + id;
                AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = new RequestParams();
                params.put("title", articleTitle);
                params.put("content", articleContent);

                client.put(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        binding.progressMessage.setVisibility(View.GONE);
                        binding.errorMessage.setVisibility(View.GONE);
                        Toast.makeText(AddUpdateArticleActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();

                        AddUpdateArticleActivity.this.setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        binding.progressMessage.setVisibility(View.GONE);
                        binding.errorMessage.setVisibility(View.VISIBLE);
                        String errorMessage = "Oops! An error occured.\n"
                                + "[" + statusCode + "] "
                                + error.getMessage();
                        binding.tvErrorMessage.setText(errorMessage);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isProgress) super.onBackPressed();
    }
}