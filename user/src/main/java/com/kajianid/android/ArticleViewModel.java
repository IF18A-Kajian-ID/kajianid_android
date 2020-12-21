package com.kajianid.android;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.android.data.Article;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

class ArticleViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Article>> listArticle = new MutableLiveData<>();

    public String setArticle(Context context, String searchQuery) {

        try {
            Looper.prepare();
        } catch (Exception e) {
        }
        final String[] ret = {null};
        String url = context.getResources().getString(R.string.server) + "api/articles";
        ArrayList<Article> listItems = new ArrayList<>();
        SyncHttpClient client = new SyncHttpClient();

        // Request Parameters
        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "0");
        params.put("query", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    Log.d("Isi", result);

                    for (int i = 0; i <= list.length(); i++) {
                        JSONObject articleJSONObject = list.getJSONObject(i);
                        Article article = new Article();

                        article.setId(articleJSONObject.getString("id"));
                        article.setTitle(articleJSONObject.getString("tittle"));
                        article.setContent(articleJSONObject.getString("content"));
                        article.setUstadzName(articleJSONObject.getString("ustadz name"));
                        article.setHasImg(articleJSONObject.getString("has_img"));

                        if (article.getHasImg().equals("1")) {
                            String extension = articleJSONObject.getString("extension");
                            article.setImgUrl(context.getResources().getString(R.string.server) + "assets/articles/" + article.getId() + "." + extension);
                        }

                        String date = articleJSONObject.getString("post_date");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        article.setPostDate(dateDueFormatted);
                        listItems.add(article);
                    }
                    listArticle.postValue(listItems);
                    ret[0] = null;
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String expected = new String(responseBody);
                ret[0] = expected;
            }
        });
        return ret[0];
    }

    public boolean setArticleAsync(Context context, ListArtikelAdapter adapter, String searchQuery) {
        final boolean[] ret = {false};
        String url = context.getResources().getString(R.string.server) + "api/articles";
        ArrayList<Article> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", "admin"); // replace admin with Ustadz ID
        params.put("query", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    Log.d("Isi", result);

                    for (int i = 0; i <= list.length(); i++) {
                        JSONObject articleJSONObject = list.getJSONObject(i);
                        Article article = new Article();

                        article.setId(articleJSONObject.getString("id"));
                        article.setTitle(articleJSONObject.getString("tittle"));
                        article.setContent(articleJSONObject.getString("content"));
                        article.setUstadzName(articleJSONObject.getString("ustadz name"));
                        article.setHasImg(articleJSONObject.getString("has_img"));

                        if (article.getHasImg().equals("1")) {
                            String extension = articleJSONObject.getString("extension");
                            article.setImgUrl(context.getResources().getString(R.string.server) + "assets/articles/" + article.getId() + "." + extension);
                        }

                        String date = articleJSONObject.getString("post_date");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        article.setPostDate(dateDueFormatted);
                        listItems.add(article);
                        adapter.notifyDataSetChanged();
                    }
                    listArticle.postValue(listItems);
                    ret[0] = true;
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context,"Failure!\n"+error.getMessage()+"\n"+responseBody+"\n"+url, Toast.LENGTH_SHORT).show();
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
