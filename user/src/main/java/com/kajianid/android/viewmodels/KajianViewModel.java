package com.kajianid.android.viewmodels;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.android.adapter.ListKajianAdapter;
import com.kajianid.android.R;
import com.kajianid.android.data.Kajian;
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

public class KajianViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Kajian>> listKajian = new MutableLiveData<>();

    public String setKajian(Context context, String searchQuery) {

        try {
            Looper.prepare();
        } catch (Exception e) {
        }
        final String[] ret = {null};
        String url = context.getResources().getString(R.string.server) + "api/kajian";
        ArrayList<Kajian> listItems = new ArrayList<>();
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

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject kajianJSONObject = list.getJSONObject(i);
                        Kajian kajian = new Kajian();

                        kajian.setId(kajianJSONObject.getString("id"));
                        kajian.setTitle(kajianJSONObject.getString("title"));
                        kajian.setUstadzName(kajianJSONObject.getString("ustadz_name"));
                        kajian.setPlace(kajianJSONObject.getString("place"));
                        kajian.setAddress(kajianJSONObject.getString("address"));
                        kajian.setMosque(kajianJSONObject.getString("mosque_name"));

                        String date = kajianJSONObject.getString("post_date");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        kajian.setDate(dateDueFormatted);
                        listItems.add(kajian);
                    }
                } catch (ParseException | JSONException e) {
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

    public boolean setKajianAsync(Context context, ListKajianAdapter adapter, String searchQuery) {
        final boolean[] ret = {false};
        String url = context.getResources().getString(R.string.server) + "api/kajian";
        ArrayList<Kajian> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

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

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject articleJSONObject = list.getJSONObject(i);
                        Kajian kajian = new Kajian();

                        kajian.setId(articleJSONObject.getString("id"));
                        kajian.setTitle(articleJSONObject.getString("title"));
                        kajian.setUstadzName(articleJSONObject.getString("ustadz name"));
                        kajian.setPlace(articleJSONObject.getString("place"));
                        kajian.setAddress(articleJSONObject.getString("address"));
                        kajian.setMosque(articleJSONObject.getString("mosque_name"));

                        String date = articleJSONObject.getString("post_date");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        kajian.setDate(dateDueFormatted);
                        listItems.add(kajian);
                        adapter.notifyDataSetChanged();
                    }
                    listKajian.postValue(listItems);
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

    public LiveData<ArrayList<Kajian>> getKajian() {
        return listKajian;
    }
}