package com.kajianid.ustadz.ui.main.kajian;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Kajian;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class KajianViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<Kajian>> listKajian = new MutableLiveData<>();

    public LiveData<ArrayList<Kajian>> getKajian() {
        return listKajian;
    }

    public String setKajian(Context context, String searchQuery) {
        try {
            Looper.prepare();
        } catch (Exception ignored) {
        }

        CredentialPreference credential = new CredentialPreference(context);
        String[] ret = { null };
        String url = context.getString(R.string.server) + "api/kajian";
        ArrayList<Kajian> listItems = new ArrayList<>();
        SyncHttpClient client = new SyncHttpClient();

        // Request Parameters
        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credential.getCredential().getUsername()); // replace admin with Ustadz ID
        params.put("q", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Kajian kajian = new Kajian();

                        kajian.setId(jsonObject.getString("id"));
                        kajian.setTitle(jsonObject.getString("kajian_title"));
                        kajian.setPlace(jsonObject.getString("place"));
                        kajian.setAddress(jsonObject.getString("address"));
                        kajian.setMosqueId(jsonObject.getString("mosque_id"));
                        kajian.setMosqueName(jsonObject.getString("mosque_name"));

                        String date = jsonObject.getString("date_due");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        kajian.setDate(dateDueFormatted);
                        kajian.setImgResource(jsonObject.getString("img_resource"));

                        listItems.add(kajian);
                    }

                    listKajian.setValue(listItems);
                    ret[0] = null;
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    ret[0] = e.getMessage();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String response = new String(responseBody);
                ret[0] = response;
            }

        });

        return ret[0];
    }

    public boolean setKajianAsync(Context context, KajianAdapter adapter, String searchQuery) {
        CredentialPreference credential = new CredentialPreference(context);
        boolean[] ret = { false };
        String url = context.getString(R.string.server) + "api/kajian";
        ArrayList<Kajian> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        // Request Parameters
        RequestParams params = new RequestParams();
        params.put("read", "0");
        params.put("ustadz_mode", "1");
        params.put("ustadz_id", credential.getCredential().getUsername()); // replace admin with Ustadz ID
        params.put("q", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jsonObject = list.getJSONObject(i);
                        Kajian kajian = new Kajian();

                        kajian.setId(jsonObject.getString("id"));
                        kajian.setTitle(jsonObject.getString("kajian_title"));
                        kajian.setPlace(jsonObject.getString("place"));
                        kajian.setAddress(jsonObject.getString("address"));
                        kajian.setMosqueId(jsonObject.getString("mosque_id"));
                        kajian.setMosqueName(jsonObject.getString("mosque_name"));

                        String date = jsonObject.getString("date_due");
                        Date dateDue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date);
                        String dateDueFormatted = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(dateDue);

                        kajian.setDate(dateDueFormatted);
                        kajian.setImgResource(jsonObject.getString("img_resource"));

                        listItems.add(kajian);
                        adapter.notifyDataSetChanged();
                    }

                    listKajian.setValue(listItems);

                    ret[0] = true;
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failure!\n" + e.getMessage() + "\n{onSuccess(), client.get()}", Toast.LENGTH_SHORT).show();
                    ret[0] = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String response = new String(responseBody);
                Toast.makeText(context, "Failure!\n" + error.getMessage() + "\n" + response, Toast.LENGTH_SHORT).show();
                ret[0] = false;
            }

        });

        return ret[0];
    }

}
