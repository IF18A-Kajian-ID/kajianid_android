package com.kajianid.android.viewmodels;

import android.content.Context;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.android.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public class ShowKajianViewModel extends ViewModel {
    private MutableLiveData<Map<String, Object>> mData = new MutableLiveData<>();

    public LiveData<Map<String, Object>> getKajian() {
        return mData;
    }

    public Map<String, Object> setKajian(Context context, String id) {
        try {
            Looper.prepare();
        } catch (Exception ignored) {

        }
        Map<String, Object> resultMap = new HashMap<>();
        String url = context.getString(R.string.server) + "api/kajian";
        SyncHttpClient client = new SyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "1");
        params.put("ustadz_mode", "0");
        params.put("kajian", id);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    JSONObject kajianJSONObject = list.getJSONObject(0);
                    String title = kajianJSONObject.getString("kajian_title");
                    String ustadzName = kajianJSONObject.getString("ustadz_name");
                    String mosqueName = kajianJSONObject.getString("mosque_name");
                    String address = kajianJSONObject.getString("address");
                    String category = kajianJSONObject.getString("place");
                    String youtubeLink = kajianJSONObject.getString("youtube_link");
                    String description = kajianJSONObject.getString("description");
                    String imgResource = kajianJSONObject.getString("img_resource");
                    String dateAnnounce = kajianJSONObject.getString("date_announce");
                    String dateDue = kajianJSONObject.getString("date_due");

                    String date = kajianJSONObject.getString("post_date");
                    Date datePost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                    assert datePost != null;
                    String datePostFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(datePost);

                    resultMap.put("status", true);
                    resultMap.put("id", id);
                    resultMap.put("ustadz_name", ustadzName);
                    resultMap.put("mosque_name", mosqueName);
                    resultMap.put("address", address);
                    resultMap.put("category", category);
                    resultMap.put("youtube_link", youtubeLink);
                    resultMap.put("post_date", datePostFormatted);
                    resultMap.put("description", description);
                    resultMap.put("img_resource", imgResource);
                    resultMap.put("date_announce", dateAnnounce);
                    resultMap.put("date_due", dateDue);
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

    public Map<String, Object> setKajianAsync(Context context, String id) {
        Map<String, Object> resultMap = new HashMap<>();
        String url = context.getString(R.string.server) + "api/kajian";
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("read", "1");
        params.put("ustadz_mode", "0");
        params.put("kajian", id);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    JSONObject kajianJSONObject = list.getJSONObject(0);
                    String title = kajianJSONObject.getString("kajian_title");
                    String ustadzName = kajianJSONObject.getString("ustadz_name");
                    String mosqueName = kajianJSONObject.getString("mosque_name");
                    String address = kajianJSONObject.getString("address");
                    String category = kajianJSONObject.getString("place");
                    String youtubeLink = kajianJSONObject.getString("youtube_link");
                    String description = kajianJSONObject.getString("description");
                    String imgResource = kajianJSONObject.getString("img_resource");
                    String dateAnnounce = kajianJSONObject.getString("date_announce");
                    String dateDue = kajianJSONObject.getString("date_due");

                    String date = kajianJSONObject.getString("post_date");
                    Date datePost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                    assert datePost != null;
                    String datePostFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(datePost);

                    resultMap.put("status", true);
                    resultMap.put("id", id);
                    resultMap.put("ustadz_name", ustadzName);
                    resultMap.put("mosque_name", mosqueName);
                    resultMap.put("address", address);
                    resultMap.put("category", category);
                    resultMap.put("youtube_link", youtubeLink);
                    resultMap.put("post_date", datePostFormatted);
                    resultMap.put("description", description);
                    resultMap.put("img_resource", imgResource);
                    resultMap.put("date_announce", dateAnnounce);
                    resultMap.put("date_due", dateDue);
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