package com.kajianid.ustadz.ui.article;

import android.content.Context;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ReadArticleViewModel extends ViewModel {

    private final MutableLiveData<Map<String, Object>> mData = new MutableLiveData<>();

    public LiveData<Map<String, Object>> getArticle() {
        return mData;
    }

    public Map<String, Object> setArticle(Context context, String id) {
        try {
            Looper.prepare();
        } catch (Exception ignored) {

        }
        Map<String, Object> resultMap = new HashMap<>();

        String url = context.getString(R.string.server) + "api/articles";
        CredentialPreference credential = new CredentialPreference(context);
        SyncHttpClient client = new SyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "1");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credential.getCredential().getUsername());
        params.put("article", id);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");
                    String like = resultObject.getString("likes");

                    JSONObject articleJSONObject = list.getJSONObject(0);
                    String title = articleJSONObject.getString("title");
                    boolean hasImg = articleJSONObject.getBoolean("has_img");
                    String extension = articleJSONObject.getString("extension");
                    String content = articleJSONObject.getString("content");

                    String date = articleJSONObject.getString("post_date");
                    Date datePost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                    assert datePost != null;
                    String datePostFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(datePost);

                    resultMap.put("status", true);
                    resultMap.put("id", id);
                    resultMap.put("title", title);
                    resultMap.put("hasImg", hasImg);
                    resultMap.put("extension", extension);
                    resultMap.put("content", content);
                    resultMap.put("post_date", datePostFormatted);
                    resultMap.put("like", like);
                    mData.postValue(resultMap);
                } catch (JSONException | ParseException e) {
                    resultMap.put("status", false);
                    resultMap.put("code", 0);
                    resultMap.put("message", e.getLocalizedMessage() + " id: " + id);
                    mData.postValue(resultMap);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                resultMap.put("status", false);
                resultMap.put("code", statusCode);
                resultMap.put("message", error.getLocalizedMessage() + " id: " + id);
                mData.postValue(resultMap);
            }
        });

        return resultMap;
    }

    public Map<String, Object> setArticleAsync(Context context, String id) {
        Map<String, Object> resultMap = new HashMap<>();

        String url = context.getString(R.string.server) + "api/articles";
        CredentialPreference credential = new CredentialPreference(context);
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "1");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credential.getCredential().getUsername());
        params.put("article", id);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");
                    String like = resultObject.getString("likes");

                    JSONObject articleJSONObject = list.getJSONObject(0);
                    String title = articleJSONObject.getString("title");
                    boolean hasImg = articleJSONObject.getBoolean("has_img");
                    String extension = articleJSONObject.getString("extension");
                    String content = articleJSONObject.getString("content");

                    String date = articleJSONObject.getString("post_date");
                    Date datePost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                    assert datePost != null;
                    String datePostFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(datePost);

                    resultMap.put("status", true);
                    resultMap.put("id", id);
                    resultMap.put("title", title);
                    resultMap.put("hasImg", hasImg);
                    resultMap.put("extension", extension);
                    resultMap.put("content", content);
                    resultMap.put("post_date", datePostFormatted);
                    resultMap.put("like", like);
                    mData.postValue(resultMap);
                } catch (JSONException | ParseException e) {
                    resultMap.put("status", false);
                    resultMap.put("code", 0);
                    resultMap.put("message", e.getLocalizedMessage() + " id: " + id);
                    mData.postValue(resultMap);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                resultMap.put("status", false);
                resultMap.put("code", statusCode);
                resultMap.put("message", error.getLocalizedMessage() + " id: " + id);
                mData.postValue(resultMap);
            }
        });

        return resultMap;
    }
}
