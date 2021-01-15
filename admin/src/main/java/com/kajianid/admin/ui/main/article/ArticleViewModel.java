package com.kajianid.admin.ui.main.article;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.admin.R;
import com.kajianid.admin.data.Article;
import com.kajianid.admin.prefs.CredentialPreference;
import com.kajianid.admin.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class ArticleViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();

    public String setArticle(Context context, String searchQuery) {
        try {
            Looper.prepare();
        } catch (Exception ignored) {

        }
        CredentialPreference credentialPreference = new CredentialPreference(context);
        final String[] ret = {null};
        String url = context.getResources().getString(R.string.server) + "api/articles";
        ArrayList<Article> listItems = new ArrayList<>();
        SyncHttpClient client = new SyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credentialPreference.getCredential().getUsername());
        params.put("query", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Article article = new Article();

                        article.setId(jsonObject.getString("id"));
                        article.setTitle(jsonObject.getString("title"));
                        article.setContent(jsonObject.getString("content"));
                        article.setHasImg(StringHelper.convertToBoolean(jsonObject.getString("has_img")));

                        if (article.isHasImg()) {
                            String extension = jsonObject.getString("extension");
                            article.setImgUrl(context.getResources().getString(R.string.server) + "assets/articles/" +
                                    article.getId() + "." + extension);
                        }

                        String date = jsonObject.getString("post_date");
                        DateFormat dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        assert dateDue != null;
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue.parse(date));

                        article.setPost_date(dateDueFormatted);

                        listItems.add(article);
                    }
                    listArticle.postValue(listItems);
                    ret[0] = null;
                } catch (JSONException | ParseException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                Toast.makeText(
                        context,
                        "Failure!\n" +
                                error.getMessage() +
                                "\n" +
                                Arrays.toString(responseBody),
                        Toast.LENGTH_LONG
                ).show();
                ret[0] = Arrays.toString(responseBody);
            }
        });

        return ret[0];
    }

    public boolean setArticleAsync(Context context, ArticleAdapter adapter, String searchQuery) {
        CredentialPreference credentialPreference = new CredentialPreference(context);
        final boolean[] ret = {false};
        String url = context.getResources().getString(R.string.server) + "api/articles";
        ArrayList<Article> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credentialPreference.getCredential().getUsername());
        params.put("query", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Article article = new Article();

                        article.setId(jsonObject.getString("id"));
                        article.setTitle(jsonObject.getString("title"));
                        article.setContent(jsonObject.getString("content"));
                        article.setHasImg(StringHelper.convertToBoolean(jsonObject.getString("has_img")));

                        if (article.isHasImg()) {
                            String extension = jsonObject.getString("extension");
                            article.setImgUrl(context.getResources().getString(R.string.server) + "assets/articles/" +
                                    article.getId() + "." + extension);
                        }

                        String date = jsonObject.getString("post_date");
                        DateFormat dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        assert dateDue != null;
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue.parse(date));

                        article.setPost_date(dateDueFormatted);

                        listItems.add(article);
                        adapter.notifyDataSetChanged();
                    }
                    listArticle.postValue(listItems);
                    ret[0] = true;
                } catch (JSONException | ParseException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(
                        context,
                        "Failure!\n" +
                                error.getMessage() +
                                "\n" +
                                Arrays.toString(responseBody),
                        Toast.LENGTH_LONG
                ).show();
                error.printStackTrace();
                ret[0] = false;
            }
        });

        return ret[0];
    }

    public LiveData<ArrayList<Article>> getArticle() {
        return listArticle;
    }
}
